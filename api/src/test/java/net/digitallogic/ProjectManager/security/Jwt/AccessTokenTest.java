package net.digitallogic.ProjectManager.security.Jwt;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class AccessTokenTest {
	private final long expires = 720;
	private final Clock clock = Clock.fixed(Clock.systemUTC()
			.instant(), ZoneId.of("UTC"));

	@Test
	void allocateTokenTest() {
		String userName = "joe@gmail.com";
		AccessToken accessToken = getAccessToken(clock);
		String token = accessToken.allocateToken(userName);

		JwtTokenBuilder.Claims claims = accessToken.getClaims(token);

		assertThat(claims.getSubject()).isEqualTo(userName);
		assertThat(claims.getIssuer()).isEqualTo("userLogin");
	}

	@Test
	void tokenValidateTest() {
		String userName = "joe@gmail.com";

		Clock offsetClock = Clock.fixed(clock.instant()
				.plus(expires - 1, ChronoUnit.MINUTES), ZoneId.of("UTC"));

		String token = getAccessToken(clock).allocateToken(userName);

		assertThat(getAccessToken(offsetClock).getClaims(token))
				.extracting(JwtTokenBuilder.Claims::getSubject, JwtTokenBuilder.Claims::getIssuer)
				.containsExactly(userName, "userLogin");
	}

	private AccessToken getAccessToken(Clock clock) {
		String tokenSecret = "superSecsdfsdfretCasdfasdfqjnmsfdlkajsdodejlaasdfaqwejklawoiejflfasdfaSfksasdfsdfasdfjef";
		String tokenClaims = "userLogin";

		return new AccessToken(
				tokenSecret,
				tokenClaims,
				expires,
				clock
		);
	}
}
