package net.digitallogic.ProjectManager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.persistence.dto.ErrorDto;
import net.digitallogic.ProjectManager.persistence.dto.ErrorDto.ErrorDtoBuilder;
import net.digitallogic.ProjectManager.web.exceptions.MessageCode;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	public RestAuthenticationFailureHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.info("Invalid authentication attempt {}", exception.getMessage());

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ErrorDtoBuilder errorBuilder = ErrorDto.builder();
		errorBuilder.path(request.getRequestURI());

		if (exception instanceof BadCredentialsException) {
			errorBuilder.code(MessageCode.AUTH_BAD_CREDENTIALS.code);
		} else if (exception instanceof DisabledException) {
			errorBuilder.code(MessageCode.AUTH_ACCOUNT_NOT_ACTIVATED.code);
		} else if (exception instanceof LockedException) {
			errorBuilder.code(MessageCode.AUTH_ACCOUNT_LOCKED.code);
		} else if (exception instanceof AccountExpiredException) {
			errorBuilder.code(MessageCode.AUTH_ACCOUNT_EXPIRED.code);
		} else if (exception instanceof CredentialsExpiredException) {
			errorBuilder.code(MessageCode.AUTH_CREDENTIALS_EXPIRED.code);
		}

		errorBuilder.message(exception.getMessage());

		objectMapper.writeValue(
				response.getWriter(),
				errorBuilder.build()
		);
	}
}
