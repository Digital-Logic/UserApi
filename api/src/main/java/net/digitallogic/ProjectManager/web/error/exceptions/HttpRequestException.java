package net.digitallogic.ProjectManager.web.error.exceptions;

import net.digitallogic.ProjectManager.web.error.ErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;



public abstract class HttpRequestException extends RuntimeException {

	private final ErrorMessage errorMessage;
	private final List<Error> errors;

	public HttpRequestException(ErrorMessage errorMessage, List<Error> errors) {
		this.errorMessage = errorMessage;
		this.errors = errors;
	}

	public HttpRequestException(ErrorMessage errorMessage) {
		this(errorMessage, Collections.emptyList());
	}

	protected HttpRequestException(HttpRequestExceptionBuilder<?, ?> b) {
		this.errorMessage = b.errorMessage;
		List<Error> errors;
		switch (b.errors == null ? 0 : b.errors.size()) {
			case 0:
				errors = Collections.emptyList();
				break;
			case 1:
				errors = Collections.singletonList(b.errors.get(0));
				break;
			default:
				errors = List.copyOf(b.errors);
		}
		this.errors = errors;
	}

	public abstract HttpStatus getHttpStatus();

	public String getErrorCode() {
		return errorMessage.getErrorCode();
	}

	public String getErrorMessage(MessageSource messageSource) {
		return errorMessage.getMessage(messageSource);
	}

	public List<Error> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	public static abstract class HttpRequestExceptionBuilder<C extends HttpRequestException, B extends HttpRequestExceptionBuilder<C, B>> {
		private ErrorMessage errorMessage;
		private ArrayList<Error> errors;

		public B errorMessage(ErrorMessage errorMessage) {
			this.errorMessage = errorMessage;
			return self();
		}

		public B error(Error error) {
			if (this.errors == null) this.errors = new ArrayList<>();
			this.errors.add(error);
			return self();
		}

		public B error(String domain, ErrorMessage errorMessage){
			return error(new Error(domain, errorMessage));
		}

		public B errors(Collection<? extends Error> errors) {
			if (this.errors == null) this.errors = new ArrayList<Error>();
			this.errors.addAll(errors);
			return self();
		}

		public B clearErrors() {
			if (this.errors != null)
				this.errors.clear();
			return self();
		}

		protected abstract B self();

		public abstract C build();

		public String toString() {return "HttpRequestException.HttpRequestExceptionBuilder(super=" + super.toString() + ", errorMessage=" + this.errorMessage + ", errors=" + this.errors + ")";}
	}
}
