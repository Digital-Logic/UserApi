package net.digitallogic.ProjectManager.security.Jwt;


import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import java.security.Key;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;


public abstract class JwtTokenBuilder {

	private final Key key;
	private final String iss;
	private final long expiration;
	private final TokenClock clock;
	private final JwtParser parser;

	public JwtTokenBuilder(String tokenSecret, String iss, long expiration, Clock clock) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
		this.iss = iss;
		this.expiration = expiration;
		this.clock = new TokenClock(clock);

		parser = Jwts.parserBuilder()
				.setClock(new TokenClock(clock))
				.setSigningKey(key)
				.setAllowedClockSkewSeconds(1)
				.requireIssuer(iss)
				.build();
	}

	public abstract String allocateToken(String subject);

	protected TokenBuilder builder() {
		return new TokenBuilder(key, iss, expiration, clock);
	}

	/**
	 * Validates a String value as a jwt token.
	 *
	 * @param token
	 * @return Claims
	 * @Throws UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException
	 */
	public Claims getClaims(String token) {
		return new Claims(token, parser, clock);
	}

	public static class Claims {
		@Getter
		private final io.jsonwebtoken.Claims claims;
		private final TokenClock clock;

		public Claims(String token, JwtParser parser, TokenClock clock) {
			this.claims = parser
					.parseClaimsJws(token)
					.getBody();
			this.clock = clock;
		}

		public String getSubject() {
			return claims.getSubject();
		}

		public LocalDateTime getExpiration() {
			return clock.from(claims.getExpiration());
		}

		public String getIssuer() {
			return claims.getIssuer();
		}
	}

	protected static final class TokenBuilder {
		private final TokenClock clock;
		private final JwtBuilder builder;

		public TokenBuilder(Key key, String iss, long expiration, TokenClock clock) {
			this.clock = clock;
			builder = Jwts.builder()
					.setIssuer(iss)
					.setExpiration(clock.now(expiration, ChronoUnit.MINUTES))
					.signWith(key, SignatureAlgorithm.HS512);

		}

		public TokenBuilder setSubject(String subject) {
			builder.setSubject(subject);
			return this;
		}

		public TokenBuilder addClaim(String key, Object value) {
			builder.claim(key, value);
			return this;
		}

		public String build() {
			return builder.compact();
		}
	}

	/**
	 * Adapter clock for JwtClock to time.Clock class
	 */
	public static class TokenClock implements io.jsonwebtoken.Clock {
		private final Clock clock;

		public TokenClock(Clock clock) {
			this.clock = clock;
		}

		@Override
		public Date now() {
			return Date.from(clock.instant());
		}

		public Date now(long amountToAdd, TemporalUnit unit) {
			return Date.from(clock.instant().plus(amountToAdd, unit));
		}

		// convert LocalDateTime to Date
		public Date from(LocalDateTime localDateTime) {
			return Date.from(
					localDateTime.atZone(clock.getZone()).toInstant()
			);
		}

		public LocalDateTime from(Date date) {
			return LocalDateTime.from(
					date.toInstant().atZone(clock.getZone())
			);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			TokenClock that = (TokenClock) o;

			return clock.equals(that.clock);
		}

		@Override
		public int hashCode() {
			return clock.hashCode();
		}
	}
}
