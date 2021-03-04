package net.digitallogic.ProjectManager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Clock;
import java.util.TimeZone;

@Configuration
@ComponentScan(value = "net.digitallogic.ProjectManager")
@Slf4j
public class AppConfig {

	@Autowired
	public AppConfig()
	{

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Primary
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public LocalValidatorFactoryBean validatorFactoryBean() {
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.setValidationMessageSource(messageSource());
		return factory;
	}

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

}
