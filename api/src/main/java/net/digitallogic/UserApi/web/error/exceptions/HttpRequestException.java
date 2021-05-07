package net.digitallogic.UserApi.web.error.exceptions;

import net.digitallogic.UserApi.web.error.ErrorCode;
import net.digitallogic.UserApi.web.Translator;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;



public abstract class HttpRequestException extends RuntimeException {

	private final ErrorCode errorCode;
	private final Translator messageTranslator;
	private final List<ErrorMessage> errorMessages;

	public HttpRequestException(ErrorCode errorCode,
	                            Translator messageTranslator,
								List<ErrorMessage> errorMessages) {

		this.errorCode = errorCode;

		this.messageTranslator = messageTranslator;
		this.errorMessages = errorMessages;
	}

	public HttpRequestException(ErrorCode errorCode,
	                            Translator messageTranslator) {
		this(errorCode, messageTranslator, Collections.emptyList());
	}

	public abstract HttpStatus getHttpStatus();

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public String getTranslatedMessage(MessageSource messageSource) {
		return messageTranslator.getTranslatedMessage(messageSource);
	}

	public List<ErrorMessage> getErrors() {
		return Collections.unmodifiableList(errorMessages);
	}
}
