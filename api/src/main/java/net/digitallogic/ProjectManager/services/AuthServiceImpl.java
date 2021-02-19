package net.digitallogic.ProjectManager.services;

import net.digitallogic.ProjectManager.persistence.biTemporal.entity.BiTemporalEntityId;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.UserStatusEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.VerificationToken;
import net.digitallogic.ProjectManager.persistence.entity.user.VerificationToken_;
import net.digitallogic.ProjectManager.persistence.repository.UserRepository;
import net.digitallogic.ProjectManager.persistence.repository.UserStatusRepository;
import net.digitallogic.ProjectManager.persistence.repository.VerificationTokenRepository;
import net.digitallogic.ProjectManager.persistence.repositoryFactory.GraphBuilder;
import net.digitallogic.ProjectManager.security.TokenGenerator;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.error.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl {

	private final UserRepository userRepository;
	private final VerificationTokenRepository tokenRepository;
	private final UserStatusRepository userStatusRepository;
	private final GraphBuilder<VerificationToken> tokenGraphBuilder;
	private final TokenGenerator activateAccountToken;
	private final TokenGenerator resetPasswordToken;
	private final Clock systemClock;

	public static final String ENABLE_ACCOUNT_TOKEN_TYPE = "EnableAccount";
	public static final String RESET_PASSWORD_TOKEN_TYPE = "ResetPassword";

	@Autowired
	public AuthServiceImpl(
			UserRepository userRepository,
			VerificationTokenRepository tokenRepository,
			UserStatusRepository userStatusRepository,
			GraphBuilder<VerificationToken> tokenGraphBuilder,
			Clock systemClock,
			@Value("${token.activateAccount.duration}") int enableAccountTokenDuration,
			@Value("${token.resetPassword.duration}") int resetPasswordTokenDuration
	) {

		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
		this.userStatusRepository = userStatusRepository;
		this.tokenGraphBuilder = tokenGraphBuilder;
		this.systemClock = systemClock;

		activateAccountToken = new TokenGenerator(ENABLE_ACCOUNT_TOKEN_TYPE,
				Duration.ofHours(enableAccountTokenDuration),
				systemClock);

		resetPasswordToken = new TokenGenerator(RESET_PASSWORD_TOKEN_TYPE,
				Duration.ofHours(resetPasswordTokenDuration),
				systemClock);
	}

	public boolean activateAccount(String encodedToken) {
		String tokenId = URLDecoder.decode(encodedToken, StandardCharsets.UTF_8);

		VerificationToken token = tokenRepository.findById(tokenId, tokenGraphBuilder.createResolver(VerificationToken_.USER))
				.orElseThrow(() ->
						new BadRequestException(ErrorCode.INVALID_TOKEN, MessageTranslator.InvalidToken()));

		UserStatusEntity status = userStatusRepository.findByEntityId(token.getUser().getId(), systemClock)
				.orElseThrow();

		if (status.isAccountEnabled())
			return true;
		else {
			// TODO Move this logic into the BiTemporalRepository Implementation
			// So that it automatically does this...
			LocalDateTime now = LocalDateTime.now(systemClock);

			// Mark curStatus as invalid by setting systemStop to current timestamp
			status.setSystemStop(now);

			// Create new entity to
			UserStatusEntity statusEntity = UserStatusEntity.builder()
					.id(BiTemporalEntityId.<UUID>builder()
							.id(token.getUser().getId())
							.validStart(now)
							.systemStart(now)
							.build())
					.accountEnabled(true)
					.accountLocked(status.isAccountLocked())
					.accountExpired(status.isAccountExpired())
					.credentialsExpired(status.isCredentialsExpired())
					.build();

			// Fill in biTemporal history
			if (statusEntity.getValidStart().isAfter(status.getValidStart())) {
				UserStatusEntity history = UserStatusEntity.builder()
						.id(BiTemporalEntityId.<UUID>builder()
								.id(token.getUser().getId())
								.validStart(status.getValidStart())
								.systemStart(now)
								.build())
						.accountEnabled(status.isAccountEnabled())
						.accountLocked(status.isAccountLocked())
						.accountExpired(status.isAccountExpired())
						.credentialsExpired(status.isCredentialsExpired())
						.build();
			}

		}
		return false;
	}

	@Transactional
	public String createActivateAccountToken(UserEntity user) {
		VerificationToken token = activateAccountToken.generate(user);
		tokenRepository.save(token);

		if (token.getId() == null)
			throw new RuntimeException();

		return URLEncoder.encode(token.getId(), StandardCharsets.UTF_8);
	}
}
