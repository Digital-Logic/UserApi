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

	@Getter
	@Nullable
	private final Exception ex;

	public HttpRequestException(HttpStatus httpStatus,
	                            MessageConverter message,
	                            @Nullable Object details,
	                            @Nullable Exception ex) {

		this.httpStatus = httpStatus;
		this.exceptionMessage = new ExceptionMessage.SingularExceptionMessage(message);
		this.details = details;
		this.ex = ex;
	}

	public HttpRequestException(HttpStatus httpStatus,
	                            Map<String, MessageConverter> messages,
	                            @Nullable Object details,
	                            @Nullable Exception ex) {
		this.httpStatus = httpStatus;
		this.exceptionMessage = new ExceptionMessage.MultiExceptionMessage(messages);
		this.details = details;
		this.ex = ex;
	}


	public HttpRequestException(HttpStatus httpStatus,
	                            MessageCode code,
	                            List<Object> args,
	                            @Nullable Object details,
	                            @Nullable Exception ex) {
		this(httpStatus, new MessageConverter(code, args), details, ex);
	}
	public HttpRequestException(HttpStatus httpStatus,
	                            MessageCode messageCode,
	                            List<Object> args,
	                            @Nullable Object details) {
		this(httpStatus, new MessageConverter(messageCode, args), details, null);
	}
	public HttpRequestException(HttpStatus httpStatus,
	                            MessageCode messageCode,
	                            Object... args) {
		this(httpStatus, new MessageConverter(messageCode, args), null, null);
	}

	public HttpRequestException(HttpStatus httpStatus,
	                            MessageConverter message,
	                            @Nullable Object details) {
		this(httpStatus, message, details, null);
	}

	public HttpRequestException(HttpStatus httpStatus,
	                            MessageConverter messageConverter) {
		this(httpStatus, messageConverter, null , null);
	}

	// Multipart

	public HttpRequestException(HttpStatus httpStatus,
	                            Map<String, MessageConverter> messages,
	                            @Nullable Object details) {
		this(httpStatus, messages, details , null);
	}

	public HttpRequestException(HttpStatus httpStatus,
	                            Map<String, MessageConverter> messages) {
		this(httpStatus, messages, null , null);
	}

	public Object getMessage(MessageSource messageSource) {
		return exceptionMessage.getMessage(messageSource);
	}




	/**
	 * Exception Message
	 */
	public static abstract class ExceptionMessage {
		public abstract Object getMessage(final MessageSource messageSource);

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
