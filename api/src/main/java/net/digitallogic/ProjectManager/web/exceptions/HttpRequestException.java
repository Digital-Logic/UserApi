package net.digitallogic.ProjectManager.web.exceptions;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestException extends RuntimeException {

	@Getter
	private final HttpStatus httpStatus;
	private final ExceptionMessage exceptionMessage;

	@Getter
	@Nullable
	private final Object details;

	public HttpRequestException(HttpStatus httpStatus,
	                            MessageConverter message,
	                            @Nullable Object details) {

		this.httpStatus = httpStatus;
		this.exceptionMessage = new ExceptionMessage.SingularExceptionMessage(message);
		this.details = details;
	}

	public HttpRequestException(HttpStatus httpStatus,
	                            Map<String, MessageConverter> messages,
	                            @Nullable Object details) {
		this.httpStatus = httpStatus;
		this.exceptionMessage = new ExceptionMessage.MultiExceptionMessage(messages);
		this.details = details;
	}

	public HttpRequestException(HttpStatus httpStatus,
	                            MessageCode messageCode,
	                            List<Object> args,
	                            @Nullable Object details) {
		this(httpStatus, new MessageConverter(messageCode, args), details);
	}
	public HttpRequestException(HttpStatus httpStatus,
	                            MessageCode messageCode,
	                            List<Object> args) {
		this(httpStatus, new MessageConverter(messageCode, args), null);
	}
	public HttpRequestException(HttpStatus httpStatus,
	                            MessageCode messageCode,
	                            Object... args) {
		this(httpStatus, new MessageConverter(messageCode, args), null);
	}

	public HttpRequestException(HttpStatus httpStatus,
	                            MessageConverter messageConverter) {
		this(httpStatus, messageConverter, null );
	}

	// Multipart

	public HttpRequestException(HttpStatus httpStatus,
	                            Map<String, MessageConverter> messages) {
		this(httpStatus, messages, null);
	}

	public Object getMessage(MessageSource messageSource) {
		return exceptionMessage.getMessage(messageSource);
	}

	public String getStatusCode() {
		return exceptionMessage.getStatusCode();
	}




	/**
	 * Exception Message
	 */
	public static abstract class ExceptionMessage {
		public abstract Object getMessage(final MessageSource messageSource);
		public abstract String getStatusCode();
		/**
		 * Singular Exception Message
		 */
		public static class SingularExceptionMessage extends ExceptionMessage {
			private final MessageConverter message;

			public SingularExceptionMessage(MessageConverter message) {
				this.message = message;
			}

			@Override
			public Object getMessage(MessageSource messageSource) {
				return messageSource.getMessage(
						message.getMessageCode(),
						message.args.toArray(),
						LocaleContextHolder.getLocale());
			}

			@Override
			public String getStatusCode() {
				return message.getStatusCode();
			}
		}

		/**
		 * MultiPart Exception Message
		 */
		public static class MultiExceptionMessage extends ExceptionMessage {
			private final Map<String, MessageConverter> messages;

			public MultiExceptionMessage(Map<String, MessageConverter> messages) {
				this.messages = messages;
			}

			@Override
			public Object getMessage(MessageSource messageSource) {
				return messages.entrySet().stream()
						.collect(Collectors.toMap(
								Map.Entry::getKey,
								entry -> getMessage(entry, messageSource)
						));
			}

			@Override
			public String getStatusCode() {
				return String.join(",", messages.values()
						.stream()
						.map(MessageConverter::getStatusCode)
						.collect(Collectors.toSet()));
			}

			private String getMessage(Map.Entry<String, MessageConverter> entry, MessageSource messageSource) {
				MessageConverter code = entry.getValue();
				return messageSource.getMessage(
						code.getMessageCode(),
						code.getArgs().toArray(),
						LocaleContextHolder.getLocale());
			}
		}
	}
}
