package net.digitallogic.ProjectManager.security.old;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.security.Jwt.JwtTokenBuilder.Claims;
import net.digitallogic.ProjectManager.security.Jwt.RefreshToken;
import net.digitallogic.ProjectManager.security.SecurityConstants;
import net.digitallogic.ProjectManager.security.UserAuthentication;
import net.digitallogic.ProjectManager.web.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class RestRememberMeServices implements RememberMeServices {

	private final UserDetailsService userDetailsService;
	private final RefreshToken refreshToken;
	private final boolean secured;

	public RestRememberMeServices(
			UserDetailsService userDetailsService,
			RefreshToken refreshToken,
			@Value("${server.ssl.enabled}") boolean secured) {

		this.userDetailsService = userDetailsService;
		this.refreshToken = refreshToken;
		this.secured = secured;
	}

	@Override
	public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
		log.info("Autologin with remember me.");

		Cookie cookie = WebUtils.getCookie(request, SecurityConstants.REFRESH_TOKEN);

		if (cookie != null) {
			try {
				String tokenStr = cookie.getValue();
				Claims claims = refreshToken.getClaims(tokenStr);

				UserAuthentication userAuthentication = (UserAuthentication) userDetailsService.loadUserByUsername(claims.getSubject());

				return new UsernamePasswordAuthenticationToken(
					userAuthentication, tokenStr, userAuthentication.getAuthorities()
				);

			} catch (ExpiredJwtException | UnsupportedJwtException |
					MalformedJwtException | SignatureException |
					IllegalArgumentException | UsernameNotFoundException ex) {
					//IOException | ServletException ex) {

				log.error("Exception: {}", ex.getMessage());
			}
		}

		return null;
	}

	@Override
	public void loginFail(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = WebUtils.getCookie(request, SecurityConstants.REFRESH_TOKEN);
		if (cookie != null) {
			cookie.setMaxAge(0);
			cookie.setValue("");
			response.addCookie(cookie);
		}
	}

	@Override
	public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
		// Add RefreshToken
		log.info("Adding RefreshToken.");
		Cookie cookie = new Cookie(SecurityConstants.REFRESH_TOKEN,refreshToken.allocateToken(successfulAuthentication.getName()));

		cookie.setHttpOnly(true);
		cookie.setSecure(secured);
		cookie.setPath(Routes.API);

		response.addCookie(cookie);
	}
}
