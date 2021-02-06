package net.digitallogic.ProjectManager.security.Jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static net.digitallogic.ProjectManager.security.Jwt.JwtTokenBuilder.Claims;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RefreshTokenTest {
	private final String iss = "loginProject";
	private final Clock fixedClock = Clock.fixed(Clock.systemUTC()
			.instant(), ZoneId.of("UTC"));
	private final long expiration = 43200;

	@Test
	void allocateTokenTest() {
		String userName = "Joe@yahoo.com";

		String token = getRefreshToken(fixedClock).allocateToken(userName);
		assertThat(token).isNotNull();
	}

	@Test
	void validateTokenTest() {
		String userName = "Joe@yahoo.com";

		String token = getRefreshToken(fixedClock).allocateToken(userName);
		assertThat(token).isNotNull();

		Claims claims = getRefreshToken(fixedClock).getClaims(token);
		assertThat(claims).extracting(Claims::getSubject, Claims::getIssuer)
				.containsExactly(userName, iss);
	}

	@Test
	void expirationTest() {
		String userName = "Joe@yahoo.com";

		String token = getRefreshToken(fixedClock).allocateToken(userName);
		Clock offset = Clock.fixed(
				fixedClock.instant().plus(expiration - 1, ChronoUnit.MINUTES),
				ZoneId.of("UTC")
		);

		assertThat(getRefreshToken(offset).getClaims(token)).isNotNull();

	}

	@Test
	void passedExpirationTest() {
		String userName = "Joe@yahoo.com";

		String token = getRefreshToken(fixedClock).allocateToken(userName);
		Clock offset = Clock.fixed(
				fixedClock.instant().plus(expiration + 1, ChronoUnit.MINUTES),
				ZoneId.of("UTC")
		);

		System.out.println("token: " + token);

		assertThatThrownBy(() ->  getRefreshToken(offset).getClaims(token))
				.isInstanceOf(ExpiredJwtException.class);
	}

	@Test
	void invalidTokenTest() {
		String userName = "Joe@yahoo.com";
		String token = new RefreshToken("superSecretRefreshCodeSuperRefreshCodeSupesSecretSecretRefreshCodeSuperSecretCodeSuperSecretRefreshCode",
				iss, expiration, fixedClock).allocateToken(userName);

		assertThatThrownBy(() -> getRefreshToken(fixedClock).getClaims(token))
				.isInstanceOf(SignatureException.class);
	}

	private RefreshToken getRefreshToken(Clock clock) {
		return new RefreshToken(
				"superSecretRefreshCodeSuperRefreshCodeSuperSecretSecretRefreshCodeSuperSecretCodeSuperSecretRefreshCode",
				iss,
				expiration,
				clock
		);
	}
}
