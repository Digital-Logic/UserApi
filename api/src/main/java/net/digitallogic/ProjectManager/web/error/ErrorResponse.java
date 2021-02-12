package net.digitallogic.ProjectManager.web.error;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import net.digitallogic.ProjectManager.web.error.exceptions.HttpRequestException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Getter
public class ErrorResponse {

	private final HttpStatus code;
	private final String reason;
	private final String message;
	private final List<Error> errors;

	public ErrorResponse(HttpStatus code, String reason, String message, List<Error> errors) {
		this.code = code;
		this.reason = reason;
		this.message = message;
		this.errors = errors;
	}

	public ErrorResponse(HttpStatus httpStatus, ErrorMessage errorMessage, MessageSource messageSource) {
		this(   httpStatus,
				errorMessage.getErrorCode(),
				errorMessage.getMessage(messageSource),
				Collections.emptyList()
		);
	}


	public ErrorResponse(HttpRequestException ex, final MessageSource messageSource) {
		this.code = ex.getHttpStatus();
		this.reason = ex.getErrorCode();
		this.message = ex.getErrorMessage(messageSource);

		this.errors = ex.getErrors()
				.stream()
				.map(error -> new Error(error, messageSource))
				.collect(Collectors.toList());
	}

	public static ErrorResponse fromDefaultAttributeMap(Map<String, Object> attributes) {
		return ErrorResponse.builder()
				.message((String) attributes.get("message"))
				.build();
	}

	public Map<String, Object> toAttributeMap() {
		return Map.ofEntries(
				entry("code", code),
				entry("reason", reason),
				entry("message", message)
		);
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Builder
	@Getter
	public static class Error {
		private final String domain;
		private final String reason;
		private final String message;

		public Error(String domain, String reason, String message) {
			this.domain = domain;
			this.reason = reason;
			this.message = message;
		}

		public Error(net.digitallogic.ProjectManager.web.error.exceptions.Error error, MessageSource messageSource) {
			this.domain = error.getDomain();
			this.reason = error.getErrorCode();
			this.message = error.getMessage(messageSource);
		}
	}
}
