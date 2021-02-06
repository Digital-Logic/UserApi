package net.digitallogic.ProjectManager.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public class NotFoundException extends HttpRequestException {

	public NotFoundException(MessageCode code, Object... args) {
		super(HttpStatus.NOT_FOUND, code, List.of(args));
	}

	public NotFoundException(MessageConverter message,
	                         @Nullable Object details) {
		super(HttpStatus.NOT_FOUND, message, details);
	}
	public NotFoundException(MessageConverter message) {
		super(HttpStatus.NOT_FOUND, message);
	}

	// Multi code
	public NotFoundException(Map<String, MessageConverter> messages,
	                           @Nullable Object details) {
		super(HttpStatus.NOT_FOUND, messages, details);
	}

	public NotFoundException(Map<String, MessageConverter> messages) {
		super(HttpStatus.NOT_FOUND, messages);
	}
}
