package net.digitallogic.ProjectManager.security;

import net.digitallogic.ProjectManager.fixtures.UserFixtures;
import net.digitallogic.ProjectManager.persistence.entity.user.UserEntity;
import net.digitallogic.ProjectManager.persistence.entity.user.VerificationToken;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;


public class TokenGeneratorTest {

	private final Clock clock = Clock.fixed(Clock.systemUTC().instant(), ZoneId.of("UTC"));
	private final TokenGenerator generator = new TokenGenerator("TOKEN_TYPE", Duration.ofMinutes(10), clock);

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
