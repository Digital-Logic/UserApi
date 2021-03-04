package net.digitallogic.ProjectManager.web.error.exceptions;

import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.Translator;
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


//	protected HttpRequestException(HttpRequestExceptionBuilder<?, ?> b) {
//		this.errorMessage = b.errorMessage;
//		List<Error> errors;
//		switch (b.errors == null ? 0 : b.errors.size()) {
//			case 0:
//				errors = Collections.emptyList();
//				break;
//			case 1:
//				errors = Collections.singletonList(b.errors.get(0));
//				break;
//			default:
//				errors = List.copyOf(b.errors);
//		}
//		this.errors = errors;
//	}

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

//	public static abstract class HttpRequestExceptionBuilder<C extends HttpRequestException, B extends HttpRequestExceptionBuilder<C, B>> {
//		private ErrorMessage errorMessage;
//		private ArrayList<Error> errors;
//
//		public B errorMessage(ErrorMessage errorMessage) {
//			this.errorMessage = errorMessage;
//			return self();
//		}
//
//		public B error(Error error) {
//			if (this.errors == null) this.errors = new ArrayList<>();
//			this.errors.add(error);
//			return self();
//		}
//
//		public B error(String domain, ErrorMessage errorMessage){
//			return error(new Error(domain, errorMessage));
//		}
//
//		public B errors(Collection<? extends Error> errors) {
//			if (this.errors == null) this.errors = new ArrayList<Error>();
//			this.errors.addAll(errors);
//			return self();
//		}
//
//		public B clearErrors() {
//			if (this.errors != null)
//				this.errors.clear();
//			return self();
//		}
//
//		protected abstract B self();
//
//		public abstract C build();
//
//		public String toString() {return "HttpRequestException.HttpRequestExceptionBuilder(super=" + super.toString() + ", errorMessage=" + this.errorMessage + ", errors=" + this.errors + ")";}
//	}
}
