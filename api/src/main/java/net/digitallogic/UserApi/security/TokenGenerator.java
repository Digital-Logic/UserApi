package net.digitallogic.UserApi.security;

import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

import static net.digitallogic.UserApi.persistence.entity.auth.VerificationToken.*;

public class TokenGenerator {

	private final TokenType tokenType;
	private final int tokenLength;
	private final SecureRandom random;
	private final Duration duration;
	private final Clock clock;

	public static final int DEFAULT_TOKEN_LENGTH = 48;

	public TokenGenerator(TokenType tokenType, Duration duration, Clock clock) {
		this(tokenType, DEFAULT_TOKEN_LENGTH, duration, clock);
	}

	public TokenGenerator(TokenType tokenType,  int tokenLength, Duration duration, Clock clock) {
		this.tokenType = tokenType;
		this.tokenLength = tokenLength;
		this.duration = duration;

		this.clock = clock;

		this.random = new SecureRandom();
	}

	public VerificationToken generate(UserEntity user) {
		return builder()
				.id(generateData(tokenLength))
				.tokenType(tokenType)
				.user(user)
				.expires(LocalDateTime.now(clock).plus(duration))
				.createdDate(LocalDateTime.now(clock))
				.build();
	}


	protected String generateData(int length) {
		byte[] data = new byte[(int) (length * 0.75)];
		random.nextBytes(data);

		return new String(Base64.getEncoder().encode(data));
	}
}
