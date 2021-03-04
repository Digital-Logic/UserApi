package net.digitallogic.ProjectManager.web.error.exceptions;

import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.Translator;
import org.springframework.http.HttpStatus;

import java.util.List;

public class NotFoundException extends HttpRequestException {

	public NotFoundException(ErrorCode errorCode, Translator messageTranslator) {
		super(errorCode, messageTranslator);
	}

	public NotFoundException(ErrorCode errorCode, Translator messageTranslator, List<ErrorMessage> errorMessages) {
		super(errorCode, messageTranslator, errorMessages);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}
}
