package net.digitallogic.ProjectManager.web.error.exceptions;

import net.digitallogic.ProjectManager.web.error.ErrorMessage;
import org.springframework.http.HttpStatus;

import java.util.List;

public class NotFoundException extends HttpRequestException {

	public NotFoundException(ErrorMessage errorMessage) {
		super(errorMessage);
	}

	public NotFoundException(ErrorMessage errorMessage, List<Error> errors) {
		super(errorMessage, errors);
	}

	protected NotFoundException(NotFoundExceptionBuilder<?, ?> b) {
		super(b);
	}

	public static NotFoundExceptionBuilder<?, ?> builder() {return new NotFoundExceptionBuilderImpl();}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}

	public static abstract class NotFoundExceptionBuilder<C extends NotFoundException, B extends NotFoundExceptionBuilder<C, B>> extends HttpRequestExceptionBuilder<C, B> {
		protected abstract B self();

		public abstract C build();

		public String toString() {return "BadRequestException.BadRequestExceptionBuilder(super=" + super.toString() + ")";}
	}

	private static final class NotFoundExceptionBuilderImpl extends NotFoundExceptionBuilder<NotFoundException, NotFoundExceptionBuilderImpl> {
		private NotFoundExceptionBuilderImpl() {}

		protected NotFoundExceptionBuilderImpl self() {return this;}

		public NotFoundException build() {return new NotFoundException(this);}
	}
}
