package net.digitallogic.ProjectManager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final String encoderId;
	private final int encodeRounds;

	public WebSecurityConfig(
			@Value("${password.encoder}") String encoderId,
			@Value("${password.encoder.rounds}") int encodeRounds) {
		this.encoderId = encoderId;
		this.encodeRounds = encodeRounds;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.httpBasic()
					.disable()
				.csrf()
					.disable()
				.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
					.anyRequest()
					.permitAll();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		Map<String, PasswordEncoder> encoders = Map.of(
				"bcrypt", new BCryptPasswordEncoder(encodeRounds)
		);

		return new DelegatingPasswordEncoder(encoderId, encoders);
	}
}
