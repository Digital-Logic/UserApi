package net.digitallogic.ProjectManager.security.old;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.security.Jwt.AccessToken;
import net.digitallogic.ProjectManager.security.SecurityConstants;
import net.digitallogic.ProjectManager.security.UserAuthentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static net.digitallogic.ProjectManager.security.Jwt.JwtTokenBuilder.Claims;

@Slf4j
public class AccessTokenProcessingFilter extends OncePerRequestFilter {

	private final AccessToken accessToken;
	private final UserDetailsService userDetailsService;

	public AccessTokenProcessingFilter(AccessToken accessToken,
	                                   UserDetailsService userDetailsService) {
		this.accessToken = accessToken;
		this.userDetailsService = userDetailsService;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {

		log.info("Process access token.");
		// Get access token
		Cookie cookie = WebUtils.getCookie(request, SecurityConstants.ACCESS_TOKEN);

		if (cookie != null) {
			String tokenStr = cookie.getValue();
			try {
				Claims claims = accessToken.getClaims(tokenStr);

				UserAuthentication authentication = (UserAuthentication) userDetailsService.loadUserByUsername(claims.getSubject());

				// set User Principal
				SecurityContextHolder.getContext()
						.setAuthentication(
								new UsernamePasswordAuthenticationToken(authentication, tokenStr, authentication.getAuthorities())
						);

			} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException | UsernameNotFoundException ex) {
				// Token is no longer valid, Clear cookie
				log.info("Error process cookie: {}", ex.getMessage());
				cookie.setMaxAge(0);
				cookie.setValue("");
				response.addCookie(cookie);
			}
		}

		filterChain.doFilter(request, response);
	}
}
