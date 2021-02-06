package net.digitallogic.ProjectManager.matchers.test;

import net.digitallogic.ProjectManager.security.Jwt.AccessToken;
import net.digitallogic.ProjectManager.security.Jwt.JwtTokenBuilder;
import net.digitallogic.ProjectManager.security.Jwt.RefreshToken;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;

import static net.digitallogic.ProjectManager.matchers.IsValidToken.validToken;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class isValidTokenTest {
	private String secret = "superSecretCodeSuperSecretCodeSuperSecretCodeSuperSecretCodeSuperSecretCodeSuperSecretCodeSuperSecretCode";
	private String iss = "tokenTest";
	private long expiration = 50;
	private Clock clock = Clock.fixed(Clock.systemUTC()
			.instant(), ZoneId.of("UTC"));

	@Test
	void validTokenTest() {
		String subject = "joe@gmail.com";
		String tokenStr = getToken().allocateToken(subject);

		assertThat(tokenStr, is(validToken(getToken())));
	}

	@Test
	void invalidTokenTest() {
		String subject = "joe@gmail.com";
		String tokenStr = new RefreshToken(
				secret.replace('S', 'R'),
				iss, expiration, clock).allocateToken(subject);

		assertThat(tokenStr, not(validToken(getToken())));
	}


	private JwtTokenBuilder getToken() {
		return new AccessToken(
				secret,
				iss,
				expiration,
				clock
		);
	}
}
