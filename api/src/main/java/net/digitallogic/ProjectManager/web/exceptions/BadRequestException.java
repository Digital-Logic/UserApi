package net.digitallogic.ProjectManager.web.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

@Getter
public class BadRequestException extends HttpRequestException {

	public BadRequestException(MessageCode code, Object... args) {
		super(HttpStatus.BAD_REQUEST, code, List.of(args));
	}

	public BadRequestException(MessageConverter message,
	                           @Nullable Object details) {
		super(HttpStatus.BAD_REQUEST, message, details);
	}

	public BadRequestException(MessageConverter message) {
		super(HttpStatus.BAD_REQUEST, message);
	}

	public BadRequestException(Map<String, MessageConverter> messages,
	                           @Nullable Object details) {
		super(HttpStatus.BAD_REQUEST, messages, details);
	}

	public BadRequestException(Map<String, MessageConverter> messages) {
		super(HttpStatus.BAD_REQUEST, messages);
	}
}
