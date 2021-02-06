package net.digitallogic.ProjectManager.matchers;

import net.digitallogic.ProjectManager.security.Jwt.JwtTokenBuilder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsValidToken extends TypeSafeMatcher<String> {

	private final JwtTokenBuilder token;

	public IsValidToken(JwtTokenBuilder token) {this.token = token;}

	@Override
	protected boolean matchesSafely(String tokenStr) {
		try {
			JwtTokenBuilder.Claims claims = token.getClaims(tokenStr);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("valid jwt token");
	}

	public static Matcher<String> validToken(JwtTokenBuilder token) {
		return new IsValidToken(token);
	}
}
