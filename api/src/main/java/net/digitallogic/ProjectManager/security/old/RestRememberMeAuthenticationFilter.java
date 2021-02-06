package net.digitallogic.ProjectManager.security.old;

import io.jsonwebtoken.lang.Assert;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class RestRememberMeAuthenticationFilter extends GenericFilterBean implements ApplicationEventPublisherAware {

	private final AuthenticationManager authenticationManager;
	private final RememberMeServices rememberMeServices;

	private ApplicationEventPublisher eventPublisher;

	@Setter
	private AuthenticationSuccessHandler successHandler;

	public RestRememberMeAuthenticationFilter(AuthenticationManager authenticationManager,
	                                          RememberMeServices rememberMeServices) {
		Assert.notNull(authenticationManager, "authenticationManager cannot be null.");
		Assert.notNull(rememberMeServices, "rememberMeServices cannot be null.");
		this.authenticationManager = authenticationManager;
		this.rememberMeServices = rememberMeServices;
	}

	@Override
	public void afterPropertiesSet() {
		org.springframework.util.Assert.notNull(authenticationManager, "authenticationManager must be specified");
		org.springframework.util.Assert.notNull(rememberMeServices, "rememberMeServices must be specified");
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

	}

	@Override
	public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}
}
