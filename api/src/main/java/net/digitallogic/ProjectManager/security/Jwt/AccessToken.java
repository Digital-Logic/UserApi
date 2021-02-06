package net.digitallogic.ProjectManager.security.Jwt;

import org.springframework.beans.factory.annotation.Value;

import java.time.Clock;


public class AccessToken extends JwtTokenBuilder {

	public AccessToken(
			@Value("${token.access.secret}") String tokenSecret,
			@Value("${token.iss}") String iss,
			@Value("${token.access.expires}") long expiration,
			Clock clock) {
		super(tokenSecret, iss, expiration, clock);
	}

	public String allocateToken(String subject) {
		return builder()
				.setSubject(subject)
				.build();
	}
}
