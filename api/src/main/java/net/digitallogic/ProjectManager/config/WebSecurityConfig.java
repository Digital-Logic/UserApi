package net.digitallogic.ProjectManager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.ProjectManager.security.SignInFilter;
import net.digitallogic.ProjectManager.web.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
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

import javax.sql.DataSource;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final String encoderId;
	private final int encodeRounds;
	private final String rememberMeKey;
	private final int rememberMeExpires;
	private final AuthenticationFailureHandler authenticationFailureHandler;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;
	private final ObjectMapper objectMapper;
	private final UserDetailsService userDetailsService;
	private final DataSource dataSource;

	@Autowired
	public WebSecurityConfig(
			@Value("${password.encoder}") String encoderId,
			@Value("${password.encoder.rounds}") int encodeRounds,
			@Value("${rememberMe.key}") String rememberMeKey,
			@Value("${rememberMe.expires}") int rememberMeExpires,
			AuthenticationSuccessHandler authenticationSuccessHandler,
			AuthenticationFailureHandler authenticationFailureHandler,
			ObjectMapper objectMapper,
			UserDetailsService userDetailsService,
			AuthenticationManagerBuilder auth,
			DataSource dataSource) throws Exception {

		this.encoderId = encoderId;
		this.encodeRounds = encodeRounds;
		this.rememberMeKey = rememberMeKey;
		this.rememberMeExpires = rememberMeExpires;
		this.authenticationSuccessHandler = authenticationSuccessHandler;
		this.authenticationFailureHandler = authenticationFailureHandler;
		this.objectMapper = objectMapper;
		this.userDetailsService = userDetailsService;
		this.dataSource = dataSource;

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
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//				.sessionAuthenticationStrategy()
				.and()
				.addFilterAt(new SignInFilter(authenticationManager(),
						authenticationFailureHandler,
						authenticationSuccessHandler,
						rememberMeServices(),
						objectMapper), UsernamePasswordAuthenticationFilter.class)

				.addFilterAt(rememberMeAuthenticationFilter(), RememberMeAuthenticationFilter.class)

				.authorizeRequests()
					.antMatchers(HttpMethod.POST, Routes.SIGN_UP_ROUTE)
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
		filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);

		return filter;
	}

	@Bean
	public RememberMeServices rememberMeServices() {
		PersistentTokenBasedRememberMeServices rememberMeServices =
				new PersistentTokenBasedRememberMeServices(rememberMeKey, userDetailsService, rememberMeRepository());

		rememberMeServices.setTokenValiditySeconds(
				(int) Duration.of(14, ChronoUnit.DAYS).toSeconds()
		);
		rememberMeServices.setTokenValiditySeconds( (int) Duration.ofMinutes(rememberMeExpires).toSeconds());

		return rememberMeServices;
	}

	@Bean
	public PersistentTokenRepository rememberMeRepository() {
		JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
		repository.setCreateTableOnStartup(false);
		repository.setDataSource(dataSource);
		return repository;
	}
}
