package net.digitallogic.ProjectManager.web.error.exceptions;

import lombok.Getter;
import net.digitallogic.ProjectManager.web.error.ErrorMessage;
import org.springframework.context.MessageSource;

public class Error {
	@Getter
	private final String domain;
	private final ErrorMessage errorMessage;

	public String getErrorCode() {
		return errorMessage.getErrorCode();
	}
	public String getMessage(MessageSource messageSource) {
		return errorMessage.getMessage(messageSource);
	}

	public Error(String domain, ErrorMessage errorMessage) {
		this.domain = domain;
		this.errorMessage = errorMessage;
	}
}
