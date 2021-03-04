package net.digitallogic.ProjectManager.services;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.events.AccountRegistrationCompletedEvent;
import net.digitallogic.ProjectManager.events.SendMailEvent;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountRequest;
import net.digitallogic.ProjectManager.persistence.dto.auth.ActivateAccountToken;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPassword;
import net.digitallogic.ProjectManager.persistence.dto.security.ResetPasswordRequest;
import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken;
import net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken_;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.repository.TokenRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserStatusRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.security.TokenGenerator;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import net.digitallogic.ProjectManager.web.error.exceptions.InternalSystemFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.digitallogic.ProjectManager.persistence.entity.auth.VerificationToken.TokenType;
import static net.digitallogic.ProjectManager.web.error.ErrorCode.ACCOUNT_DISABLED;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final TokenRepository tokenRepository;
	private final UserRepository userRepository;
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
			UserRepository userRepository,
			UserStatusRepository userStatusRepository,
			ApplicationEventPublisher eventPublisher,
			PasswordEncoder encoder,
			GraphBuilder<VerificationToken> tokenGraphBuilder,
			Clock systemClock,
			@Value("${token.activateAccount.duration}") int enableAccountTokenDuration,
			@Value("${token.resetPassword.duration}") int resetPasswordTokenDuration
	) {
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
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

	/**
	 * Activate a users account is the user provides a valid account activate token
	 * @param activateAccountToken contains token to activate user account
	 * @return boolean
	 */
	@Transactional
	@Override
	public void activateAccount(ActivateAccountToken activateAccountToken) {
		String tokenId = URLDecoder.decode(activateAccountToken.getToken(), StandardCharsets.UTF_8);
		log.info("Activate account with token: {}", tokenId);

		VerificationToken token = tokenRepository.findByIdAndTokenType(tokenId, TokenType.ENABLE_ACCOUNT, LocalDateTime.now(systemClock),
				tokenGraphBuilder.createResolver(VerificationToken_.user)
		)
				.orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID, MessageTranslator.invalidAccountActivationToken()));

		// check if token has expired
		if (token.getExpires().isBefore(LocalDateTime.now(systemClock))) {
			throw new BadRequestException(ErrorCode.TOKEN_EXPIRED,
					MessageTranslator.TokenIsExpired());
		}

		// Increment used counter
		token.setUsedCount(token.getUsedCount() + 1);

		UserStatusEntity status = userStatusRepository.findByEntityId(token.getUser()
				.getId(), systemClock)
				.orElseThrow(() -> {
					log.error("Invalid user status encountered during account activation! token: {}, user: {}",
							token.getId(), token.getUser().getEmail());

					return new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR,
							MessageTranslator.InternalServerError());
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
	}

	/**
	 * Reset the users password
	 * @param resetPassword Reset password info
	 * @return boolean
	 */
	@Transactional
	@Override
	public void resetPassword(ResetPassword resetPassword) {

		// Load the token and the user entity
		VerificationToken token = tokenRepository.findByIdAndTokenType(resetPassword.getToken(),
				TokenType.RESET_PASSWORD,
				tokenGraphBuilder.createResolver(VerificationToken_.user)
		)
				.orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID, MessageTranslator.TokenIsInvalid()));

		// Has this token been used before
		if (token.getUsedCount() > 1) {
			throw new BadRequestException(ErrorCode.TOKEN_USED, MessageTranslator.TokenIsUsed());
		}
		// Check if token is expired.
		if (token.getExpires().isBefore(LocalDateTime.now(systemClock))) {
			throw new BadRequestException(ErrorCode.TOKEN_EXPIRED, MessageTranslator.TokenIsExpired());
		}

		UserEntity user = token.getUser();

		if (!user.getEmail()
				.equalsIgnoreCase(resetPassword.getEmail())) {
			throw new BadRequestException(ErrorCode.TOKEN_INVALID, MessageTranslator.TokenIsInvalid());
		}

		// Verify that the users account is not disabled, locked, or expired.
		verifyUserStatusOnPasswordReset(user);

		user.setPassword(encoder.encode(resetPassword.getPassword()));
	}

	@Async
	@TransactionalEventListener
	public void onUserRegistrationCompleted(AccountRegistrationCompletedEvent event) {
		createAccountActivationToken(event.getUser(), event.getRequest());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void accountActivateRequest(ActivateAccountRequest activateRequest) {
		UserEntity user = getUserEntity(activateRequest.getEmail());

		UserStatusEntity status = getUserStatus(user);

		createAccountActivationToken(
				user,
				((ServletRequestAttributes)
						Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest()
		);
	}

	private UserEntity getUserEntity(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new BadRequestException(
						ErrorCode.NON_EXISTENT_ENTITY,
						MessageTranslator.NonExistentEntity("User", email))
				);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW) // Create a new transaction separate from any other.
	protected void createAccountActivationToken(UserEntity user, HttpServletRequest request) {
		// UserEntity is in a Detached state.
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
							ServletUriComponentsBuilder.fromContextPath(request)
									.queryParam("activate-account", tokenStr)
										.build()
										.toUriString()
						)
						.build()
		);
	}

	@Override
	@Transactional
	public void createResetPasswordToken(ResetPasswordRequest resetRequest) {

		// Get the user entity or else throw BadRequest
		UserEntity user = getUserEntity(resetRequest.getEmail());

		// Throw BadRequest if user status is disabled, Locked, expired.
		verifyUserStatusOnPasswordReset(user);


		VerificationToken token = resetPasswordToken.generate(user);

		tokenRepository.save(token);


		String tokenStr = URLEncoder.encode(
				Objects.requireNonNull(token.getId()),
				StandardCharsets.UTF_8);

		eventPublisher.publishEvent(
				SendMailEvent.builder()
						.source(this)
						.templateName("reset-account-password")
						.fromEmail("noreplay@pr")
						.recipientEmail(user.getEmail())
						.subject("Reset account password")
						.addVariable("name", user.getFirstName() + ' ' + user.getLastName())
						.addVariable("resetPasswordLink",
								ServletUriComponentsBuilder.fromCurrentContextPath()
									.queryParam("reset-password", tokenStr)
										.build()
										.toUriString()
						)
						.build()
		);
	}

	/**
	 *	Checks the users current status, if account is disabled, locked, or expired
	 *  throw new BadRequestException
	 * @param user UserEntity
	 */
	private void verifyUserStatusOnPasswordReset(UserEntity user) {
		ErrorCode code = checkUserStatus(user);
		if (code != null) {
			switch (code) {
				case ACCOUNT_DISABLED:
					throw new BadRequestException(ErrorCode.ACCOUNT_DISABLED, MessageTranslator.ResetPasswordAccountDisabled());
				case ACCOUNT_LOCKED:
					throw new BadRequestException(ErrorCode.ACCOUNT_LOCKED, MessageTranslator.ResetPasswordAccountLocked());
				case ACCOUNT_EXPIRED:
					throw new BadRequestException(ErrorCode.ACCOUNT_EXPIRED, MessageTranslator.ResetPasswordAccountExpired());
				case CREDENTIALS_EXPIRED:
					throw new BadRequestException(ErrorCode.CREDENTIALS_EXPIRED, MessageTranslator.ResetPasswordCredentialsExpired());
			}
		}
	}

	private ErrorCode checkUserStatus(UserEntity user) {

		UserStatusEntity status = getUserStatus(user);

		if (!status.isAccountEnabled())
			return ACCOUNT_DISABLED;

		if (status.isAccountLocked())
			return ErrorCode.ACCOUNT_LOCKED;

		if (status.isAccountExpired())
			return ErrorCode.ACCOUNT_EXPIRED;

		if (status.isCredentialsExpired())
			return ErrorCode.CREDENTIALS_EXPIRED;

		return null;
	}

	private UserStatusEntity getUserStatus(UserEntity user) {
		return userStatusRepository.findByEntityId(user.getId(), systemClock)
				.orElseThrow(() -> {
					log.error("User {}, has undefined status state.", user.getEmail());
					return new InternalSystemFailure(ErrorCode.INTERNAL_SERVER_ERROR, MessageTranslator.InternalServerError());
				});
	}
}