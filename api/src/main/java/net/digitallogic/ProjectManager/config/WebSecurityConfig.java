package net.digitallogic.ProjectManager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.ProjectManager.security.SignInFilter;
import net.digitallogic.ProjectManager.web.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.time.Duration;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final String encoderId;
	private final int encodeRounds;

	private final String rememberMeKey;
	private final int rememberMeExpires;
	private final String rememberMeCookieName;
	private final String errorPath;

	private final AuthenticationFailureHandler authenticationFailureHandler;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;
	private final ObjectMapper objectMapper;
	private final UserDetailsService userDetailsService;
	private final DataSource dataSource;

	@Autowired
	public WebSecurityConfig(
			@Value("${password.encoder}") String encoderId,
			@Value("${password.encoder.rounds}") int encodeRounds,
			@Value("${token.rememberMe.key}") String rememberMeKey,
			@Value("${rememberMe.cookie.name}") String rememberMeCookieName,
			@Value("${rememberMe.expires}") int rememberMeExpires,
			@Value("${server.error.path}") String errorPath,
			AuthenticationSuccessHandler authenticationSuccessHandler,
			AuthenticationFailureHandler authenticationFailureHandler,
			ObjectMapper objectMapper,
			UserDetailsService userDetailsService,
			AuthenticationManagerBuilder auth,
			DataSource dataSource) throws Exception {

		this.encoderId = encoderId;
		this.encodeRounds = encodeRounds;
		this.rememberMeKey = rememberMeKey;
		this.rememberMeCookieName = rememberMeCookieName;
		this.rememberMeExpires = rememberMeExpires;
		this.errorPath = errorPath;
		this.authenticationSuccessHandler = authenticationSuccessHandler;
		this.authenticationFailureHandler = authenticationFailureHandler;
		this.objectMapper = objectMapper;
		this.userDetailsService = userDetailsService;
		this.dataSource = dataSource;

		auth.authenticationProvider(rememberMeAuthenticationProvider());
		auth.userDetailsService(userDetailsService);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.httpBasic()
					.disable()
				.csrf()
					.disable()

				.userDetailsService(userDetailsService)

				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
				.and()
				.addFilterAt(new SignInFilter(authenticationManager(),
						authenticationFailureHandler,
						authenticationSuccessHandler,
						rememberMeServices(),
						objectMapper), UsernamePasswordAuthenticationFilter.class)

				.addFilterAt(rememberMeAuthenticationFilter(), RememberMeAuthenticationFilter.class)

				// Logout configuration
				.logout()
					.logoutUrl(Routes.LOGOUT_ROUTE)
					.deleteCookies(rememberMeCookieName)
					.invalidateHttpSession(true)
					.logoutSuccessHandler(((request, response, authentication) -> {
						response.setStatus(HttpServletResponse.SC_ACCEPTED);
					}))
				.and()

				.authorizeRequests()
					.antMatchers(HttpMethod.POST, Routes.SIGN_UP_ROUTE)
						.permitAll()
					.antMatchers(HttpMethod.POST, Routes.ACTIVATE_ACCOUNT_ROUTE)
						.permitAll()
					.antMatchers(HttpMethod.GET, Routes.LOGOUT_ROUTE)
						.authenticated()

					// Deny all access to the error controller
					.antMatchers(errorPath)
						//.denyAll()
						.permitAll()

					.anyRequest()
						.authenticated()
		;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		Map<String, PasswordEncoder> encoders = Map.of(
				"bcrypt", new BCryptPasswordEncoder(encodeRounds)
		);

		return new DelegatingPasswordEncoder(encoderId, encoders);
	}

	@Bean
	public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception {
		RememberMeAuthenticationFilter filter = new RememberMeAuthenticationFilter(
				authenticationManager(), rememberMeServices()
		);
		return filter;
	}

	@Bean
	public RememberMeServices rememberMeServices() {
		PersistentTokenBasedRememberMeServices rememberMeServices =
				new PersistentTokenBasedRememberMeServices(rememberMeKey, userDetailsService, rememberMeRepository());


		rememberMeServices.setCookieName(rememberMeCookieName);
		rememberMeServices.setTokenValiditySeconds(
				// Convert duration of days into seconds,
				// toSeconds method returns a long, requires casting to (int)
				(int) Duration.ofDays(rememberMeExpires).toSeconds());

		rememberMeServices.setTokenValiditySeconds(32);
		rememberMeServices.setTokenLength(32);


		return rememberMeServices;
	}

	@Bean
	public PersistentTokenRepository rememberMeRepository() {
		JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
		repository.setCreateTableOnStartup(false);
		repository.setDataSource(dataSource);
		return repository;
	}

	public AuthenticationProvider rememberMeAuthenticationProvider() {
		return new RememberMeAuthenticationProvider(rememberMeKey);
	}
}
