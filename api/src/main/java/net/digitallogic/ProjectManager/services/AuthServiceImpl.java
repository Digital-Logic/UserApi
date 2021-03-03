package net.digitallogic.ProjectManager.services;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.events.CreateAccountActivationToken;
import net.digitallogic.ProjectManager.events.SendMailEvent;
import net.digitallogic.ProjectManager.persistence.dto.user.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.user.ResetPasswordRequest;
import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken;
import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.repository.TokenRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserStatusRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.security.TokenGenerator;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken.TokenType;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final TokenRepository tokenRepository;
	private final UserStatusRepository userStatusRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final PasswordEncoder encoder;
	private final GraphBuilder<VerificationToken> tokenGraphBuilder;
	private final TokenGenerator activateAccountToken;
	private final TokenGenerator resetPasswordToken;
	private final Clock systemClock;

	@Autowired
	public AuthServiceImpl(
			TokenRepository tokenRepository,
			UserStatusRepository userStatusRepository,
			ApplicationEventPublisher eventPublisher,
			PasswordEncoder encoder,
			GraphBuilder<VerificationToken> tokenGraphBuilder,
			Clock systemClock,
			@Value("${token.activateAccount.duration}") int enableAccountTokenDuration,
			@Value("${token.resetPassword.duration}") int resetPasswordTokenDuration
	) {
		this.tokenRepository = tokenRepository;
		this.userStatusRepository = userStatusRepository;
		this.eventPublisher = eventPublisher;
		this.encoder = encoder;
		this.tokenGraphBuilder = tokenGraphBuilder;
		this.systemClock = systemClock;

		activateAccountToken = new TokenGenerator(TokenType.ENABLE_ACCOUNT,
				Duration.ofHours(enableAccountTokenDuration),
				systemClock);

		resetPasswordToken = new TokenGenerator(TokenType.RESET_PASSWORD,
				Duration.ofHours(resetPasswordTokenDuration),
				systemClock);
	}

	@Transactional
	@Override
	public boolean activateAccount(ActivateAccountRequest activateAccountRequest) {
		String tokenId = URLDecoder.decode(activateAccountRequest.getCode(), StandardCharsets.UTF_8);
		log.info("Activate account with token: {}", tokenId);

		VerificationToken token = tokenRepository.findByIdAndTokenType(tokenId, TokenType.ENABLE_ACCOUNT, LocalDateTime.now(systemClock),
				tokenGraphBuilder.createResolver(VerificationToken_.user)
		)
				.orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_TOKEN, MessageTranslator.invalidAccountActivationToken()));

		// Increment used counter
		token.setUsedCount(token.getUsedCount() + 1);

		UserStatusEntity status = userStatusRepository.findByEntityId(token.getUser()
				.getId(), systemClock)
				.orElseThrow(() -> {
					log.error("Invalid user status encountered during account activation! token: {}, user: {}",
							token.getId(), token.getUser()
									.getEmail());
					return new BadRequestException(ErrorCode.INVALID_TOKEN, MessageTranslator.InvalidToken());
				});

		if (!status.isAccountEnabled()) {
			// So that it automatically does this...
			LocalDateTime now = LocalDateTime.now(systemClock);

			// Mark curStatus as invalid by setting systemStop to current timestamp
			status.setSystemStop(now);

			List<UserStatusEntity> newStatus = new ArrayList<>();

			if (status.getValidStart()
					.isBefore(now)) {
				newStatus.add(UserStatusEntity.builder()
						.user(token.getUser())
						.systemStart(now)
						.validStart(status.getValidStart())
						.validStop(now)
						.accountExpired(status.isAccountExpired())
						.accountEnabled(status.isAccountEnabled())
						.accountLocked(status.isAccountLocked())
						.credentialsExpired(status.isCredentialsExpired())
						.createdBy(token.getUser().getId())
						.build()
				);
			}
			// enable the account
			newStatus.add(
					UserStatusEntity.builder()
							.user(token.getUser())
							.systemStart(now)
							.validStart(now)
							.accountEnabled(true)
							.accountLocked(status.isAccountLocked())
							.accountExpired(status.isAccountExpired())
							.credentialsExpired(status.isCredentialsExpired())
							.createdBy(status.getCreatedBy())
							.build()
			);

			userStatusRepository.saveAll(newStatus);
		}

		return true;
	}

	@Transactional
	@Override
	public boolean resetPassword(String encodedToken, ResetPasswordRequest resetPassword) {

		VerificationToken token = tokenRepository.findById(encodedToken,
				tokenGraphBuilder.createResolver(VerificationToken_.USER)
		)
				.orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_TOKEN, MessageTranslator.InvalidToken()));

		UserEntity user = token.getUser();

		if (!user.getEmail()
				.equalsIgnoreCase(resetPassword.getEmail())) {
			throw new BadRequestException(ErrorCode.INVALID_TOKEN, MessageTranslator.InvalidToken());
		}

		user.setPassword(encoder.encode(resetPassword.getPassword()));

		return true;
	}

	@Override
	@Async		// Run this event async
	@Transactional(propagation = Propagation.REQUIRES_NEW) // Create a new transaction separate from any other.
	@TransactionalEventListener
	public void createAccountActivationToken(CreateAccountActivationToken event) {
		// UserEntity is in a Detached state.
		UserEntity user = event.getUser();
		VerificationToken token = activateAccountToken.generate(user);
		tokenRepository.save(token);

		if (token.getId() == null)
			throw new RuntimeException();

		String tokenStr = URLEncoder.encode(token.getId(), StandardCharsets.UTF_8);

		eventPublisher.publishEvent(
				SendMailEvent.builder()
						.source(this)
						.templateName("account-activation")
						.fromEmail("noreplay@pr")
						.recipientEmail(user.getEmail())
						.subject("Account Activation Required")
						.addVariable("name", user.getFirstName() + ' ' + user.getLastName())
						.addVariable("activationLink",
							ServletUriComponentsBuilder.fromContextPath(event.getRequest() )
									.queryParam("activate", tokenStr)
										.build()
										.toUriString()
						)
						.build()
		);
	}

	@Transactional
	@Override
	public String createResetPasswordToken(UserEntity user) {
		VerificationToken token = resetPasswordToken.generate(user);
		tokenRepository.save(token);

		if (token.getId() == null)
			throw new RuntimeException();

		String tokenStr = URLEncoder.encode(token.getId(), StandardCharsets.UTF_8);

		eventPublisher.publishEvent(
				SendMailEvent.builder()
						.source(this)
						.templateName("account-activation")
						.fromEmail("noreplay@pr")
						.recipientEmail(user.getEmail())
						.subject("Account Activation Required")
						.addVariable("name", user.getFirstName() + " " + user.getLastName())
						.addVariable("activationLink",
								ServletUriComponentsBuilder.fromCurrentContextPath()
									.queryParam("activate", tokenStr)
										.build()
										.toUriString()
						)
						.build()
		);
		return tokenStr;
	}
}
