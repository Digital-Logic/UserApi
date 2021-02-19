package net.digitallogic.ProjectManager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper objectMapper;
	private final MessageSource messageSource;

	@Autowired
	public RestAuthenticationFailureHandler(ObjectMapper objectMapper,
	                                        MessageSource messageSource) {
		this.objectMapper = objectMapper;
		this.messageSource = messageSource;
	}

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.info("Invalid authentication attempt {}", exception.getMessage());

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);


		MessageTranslator messageTranslator;

		if (exception instanceof BadCredentialsException) {
			messageTranslator = MessageTranslator.BadCredentials();
		} else if (exception instanceof DisabledException) {
			messageTranslator = MessageTranslator.AccountDisabled();
		} else if (exception instanceof LockedException) {
			messageTranslator = MessageTranslator.AccountLocked();
		} else if (exception instanceof AccountExpiredException) {
			messageTranslator = MessageTranslator.AccountExpired();
		} else if (exception instanceof CredentialsExpiredException) {
			messageTranslator = MessageTranslator.CredentialsExpired();
		} else {
			log.error("Auth failed for unknown reason: {}", exception.getMessage());
			messageTranslator = MessageTranslator.BadCredentials();
		}

		objectMapper.writeValue(
				response.getWriter(),
				new ErrorResponse(HttpStatus.UNAUTHORIZED,
						ErrorCode.AUTHENTICATION_FAILURE,
						messageTranslator,
						messageSource)
		);
	}
}
