package net.digitallogic.ProjectManager.security.old;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.security.Jwt.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final AccessToken accessToken;
	private final boolean secured;

	public RestAuthenticationSuccessHandler(AccessToken accessToken,
	                                        @Value("${server.ssl.enabled}") boolean secured) {

		this.accessToken = accessToken;
		this.secured = secured;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {

//		UserAuthentication userAuthentication = (UserAuthentication) authentication.getPrincipal();

//		log.info("Adding AccessToken");
//		Cookie cookie = new Cookie(SecurityConstants.ACCESS_TOKEN,
//			accessToken.allocateToken(userAuthentication.getUsername())
//		);
//		cookie.setMaxAge(-1); // Cookie has session lifespan
//		cookie.setHttpOnly(true);
//		cookie.setPath(Routes.API);
//		cookie.setSecure(secured);
//
//		response.addCookie(cookie);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    FilterChain chain,
	                                    Authentication authentication) throws IOException, ServletException {
		onAuthenticationSuccess(request, response, authentication);
	}
}
