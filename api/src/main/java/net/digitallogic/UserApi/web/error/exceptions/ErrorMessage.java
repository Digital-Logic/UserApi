package net.digitallogic.UserApi.web.error.exceptions;

import lombok.Getter;
import net.digitallogic.UserApi.web.error.ErrorCode;
import net.digitallogic.UserApi.web.Translator;


@Getter
public class ErrorMessage {

	private final String domain;
	private final ErrorCode errorCode;
	private final Translator messageTranslator;

	public ErrorMessage(String domain, ErrorCode errorCode, Translator messageTranslator) {
		this.domain = domain;
		this.errorCode = errorCode;
		this.messageTranslator = messageTranslator;
	}
}
