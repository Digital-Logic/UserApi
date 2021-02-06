package net.digitallogic.ProjectManager.security.Jwt;

import net.digitallogic.ProjectManager.security.Jwt.JwtTokenBuilder.TokenClock;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class JwtTokenBuilderTest {

	private Clock clock = Clock.fixed(Clock.systemUTC().instant(), ZoneId.of("UTC"));

	@Test
	void tokenClockTest() throws InterruptedException {
		Date date = Date.from(clock.instant());
		sleep(10);

		TokenClock tokenClock = new TokenClock(clock);
		assertThat(tokenClock.now()).isEqualTo(date);
	}

	@Test
	void tokenClockOffsetTest() {
		TokenClock tokenClock = new TokenClock(clock);
		assertThat(tokenClock.now(100, ChronoUnit.MILLIS).toInstant())
				.isCloseTo(clock.instant().plusMillis(100), within(1, ChronoUnit.MILLIS));

	}
}
