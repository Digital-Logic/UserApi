package net.digitallogic.ProjectManager.web.error;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.web.Translator;
import net.digitallogic.ProjectManager.web.error.exceptions.ErrorMessage;
import net.digitallogic.ProjectManager.web.error.exceptions.HttpRequestException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Getter
@Slf4j
public class ErrorResponse {

	private final HttpStatus code;
	private final ErrorCode reason;
	private final String message;
	private final List<ErrorBlock> errors;

	public ErrorResponse(HttpStatus code, ErrorCode reason, @Nullable String message, List<ErrorBlock> errors) {
		this.code = code;
		this.reason = reason;
		this.message = message;
		this.errors = errors;
	}

	public ErrorResponse(HttpStatus httpStatus, ErrorCode reason, Translator messageTranslator, MessageSource messageSource) {
		this(   httpStatus,
				reason,
				messageTranslator.getTranslatedMessage(messageSource),
				Collections.emptyList()
		);
	}


	public ErrorResponse(HttpRequestException ex, final MessageSource messageSource) {
		this.code = ex.getHttpStatus();
		this.reason = ex.getErrorCode();
		this.message = ex.getTranslatedMessage(messageSource);

		this.errors = ex.getErrors()
				.stream()
				.map(error -> new ErrorBlock(error, messageSource))
				.collect(Collectors.toList());
	}

	public static ErrorResponse fromDefaultAttributeMap(Map<String, Object> attributes) {
		log.info("Create ErrorResponse from attributeMap.");
		return ErrorResponse.builder()
				.code(HttpStatus.valueOf((int) attributes.getOrDefault("status", 403)))
				.reason(ErrorCode.SYSTEM_FAILURE)
				.message((String) attributes.get("message"))
				.build();
	}

	public Map<String, Object> toAttributeMap() {
		log.info("Creating AttributeMap from ErrorResponse.");
		return Map.ofEntries(
				entry("code", code),
				entry("reason", reason),
				entry("message", message)
		);
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Builder
	@Getter
	public static class ErrorBlock {
		private final String domain;
		private final ErrorCode reason;
		private final String message;

		public ErrorBlock(String domain, ErrorCode reason, String message) {
			this.domain = domain;
			this.reason = reason;
			this.message = message;
		}

		public ErrorBlock(ErrorMessage errorMessage, MessageSource messageSource) {
			this.domain = errorMessage.getDomain();
			this.reason = errorMessage.getErrorCode();
			this.message = errorMessage.getMessageTranslator().getTranslatedMessage(messageSource);
		}
	}
}
