package net.digitallogic.UserApi.security;

import net.digitallogic.UserApi.fixtures.UserFixtures;
import net.digitallogic.UserApi.persistence.entity.user.UserEntity;
import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;


public class TokenGeneratorTest {

	private final Clock clock = Clock.fixed(Clock.systemUTC().instant(), ZoneId.of("UTC"));
	private final TokenGenerator generator = new TokenGenerator(VerificationToken.TokenType.ENABLE_ACCOUNT, Duration.ofMinutes(10), clock);

	@Test
	void generateRandomDataTest() {
		String data = generator.generateData(64);
		assertThat(data).hasSize(64);

	}

	@Test
	void generateTokenTest() {
		UserEntity user = UserFixtures.userEntity();
		VerificationToken token = generator.generate(user);
		assertThat(token).isNotNull();
	}
}
