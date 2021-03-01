package net.digitallogic.ProjectManager.services;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.events.SendMailEvent;
import net.digitallogic.ProjectManager.persistence.dto.user.ResetPasswordRequest;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.VerificationToken;
import net.digitallogic.ProjectManager.persistence.entity.user.VerificationToken_;
import net.digitallogic.ProjectManager.persistence.repository.UserStatusRepository;
import net.digitallogic.ProjectManager.persistence.repository.VerificationTokenRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.security.TokenGenerator;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final VerificationTokenRepository tokenRepository;
	private final UserStatusRepository userStatusRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final PasswordEncoder encoder;
	private final GraphBuilder<VerificationToken> tokenGraphBuilder;
	private final TokenGenerator activateAccountToken;
	private final TokenGenerator resetPasswordToken;
	private final Clock systemClock;

	public static final String ENABLE_ACCOUNT_TOKEN_TYPE = "EnableAccount";
	public static final String RESET_PASSWORD_TOKEN_TYPE = "ResetPassword";

	@Autowired
	public AuthServiceImpl(
			VerificationTokenRepository tokenRepository,
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

		activateAccountToken = new TokenGenerator(ENABLE_ACCOUNT_TOKEN_TYPE,
				Duration.ofHours(enableAccountTokenDuration),
				systemClock);

		resetPasswordToken = new TokenGenerator(RESET_PASSWORD_TOKEN_TYPE,
				Duration.ofHours(resetPasswordTokenDuration),
				systemClock);
	}

	@Transactional
	@Override
	public boolean activateAccount(String encodedToken) {
		String tokenId = URLDecoder.decode(encodedToken, StandardCharsets.UTF_8);

		VerificationToken token = tokenRepository.findById(tokenId,
				tokenGraphBuilder.createResolver(VerificationToken_.USER)
		)
				.orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_TOKEN, MessageTranslator.InvalidToken()));

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
							.accountEnabled(status.isAccountExpired())
							.credentialsExpired(status.isCredentialsExpired())
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

	@Transactional
	@Override
	public String createAccountActivationToken(UserEntity user) {
		VerificationToken token = activateAccountToken.generate(user);
		tokenRepository.save(token);

		if (token.getId() == null)
			throw new RuntimeException();

		String tokenStr = URLEncoder.encode(token.getId(), StandardCharsets.UTF_8);

		eventPublisher.publishEvent(
				SendMailEvent.builder()
						.source(this)
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

	@Transactional
	@Override
	public String createResetPasswordToken(UserEntity user) {
		VerificationToken token = resetPasswordToken.generate(user);
		tokenRepository.save(token);

		if (token.getId() == null)
			throw new RuntimeException();

		return URLEncoder.encode(token.getId(), StandardCharsets.UTF_8);
	}
}
