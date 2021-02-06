package net.digitallogic.ProjectManager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.persistence.dto.user.LoginRequest;
import net.digitallogic.ProjectManager.persistence.dto.user.UserAuthenticationDto;
import net.digitallogic.ProjectManager.web.Routes;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class SignInFilter extends AbstractAuthenticationProcessingFilter {

	private final ObjectMapper objectMapper;

	public SignInFilter(AuthenticationManager authenticationManager,
	                    AuthenticationFailureHandler failureHandler,
	                    AuthenticationSuccessHandler successHandler,
	                    RememberMeServices rememberMeServices,
	                    ObjectMapper objectMapper) {

		super(new AntPathRequestMatcher(Routes.LOGIN_ROUTE, HttpMethod.POST.name()));

		this.objectMapper = objectMapper;
		setAuthenticationManager(authenticationManager);
		setAuthenticationFailureHandler(failureHandler);
		setAuthenticationSuccessHandler(successHandler);
		setRememberMeServices(rememberMeServices);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		LoginRequest login = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

		log.info("Authentication attempt for user {}", login.getEmail());

		return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
						login.getEmail(),
						login.getPassword()
				)
		);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
	                                        HttpServletResponse response,
	                                        FilterChain chain,
	                                        Authentication authResult) throws IOException, ServletException {

		super.successfulAuthentication(request, response, chain, authResult);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		objectMapper.writeValue(response.getWriter(),
				new UserAuthenticationDto((UserAuthentication) authResult.getPrincipal())
		);
	}
}
