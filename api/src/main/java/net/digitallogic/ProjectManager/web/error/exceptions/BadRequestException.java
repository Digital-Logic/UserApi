package net.digitallogic.ProjectManager.web.error.exceptions;

import net.digitallogic.ProjectManager.web.error.ErrorCode;
import net.digitallogic.ProjectManager.web.Translator;
import org.springframework.http.HttpStatus;

import java.util.List;

public class BadRequestException extends HttpRequestException {

	public BadRequestException(ErrorCode errorCode, Translator messageTranslator) {
		super(errorCode, messageTranslator);
	}

	public BadRequestException(ErrorCode errorCode,
	                           Translator messageTranslator,
	                           List<ErrorMessage> errorMessages) {
		super(errorCode, messageTranslator, errorMessages);
	}

//	protected BadRequestException(BadRequestExceptionBuilder<?, ?> b) {
//		super(b);
//	}

//	public static BadRequestExceptionBuilder<?, ?> builder() {return new BadRequestExceptionBuilderImpl();}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}

//	public static abstract class BadRequestExceptionBuilder<C extends BadRequestException, B extends BadRequestExceptionBuilder<C, B>> extends HttpRequestExceptionBuilder<C, B> {
//		protected abstract B self();
//
//		public abstract C build();
//
//		public String toString() {return "BadRequestException.BadRequestExceptionBuilder(super=" + super.toString() + ")";}
//	}

//	private static final class BadRequestExceptionBuilderImpl extends BadRequestExceptionBuilder<BadRequestException, BadRequestExceptionBuilderImpl> {
//		private BadRequestExceptionBuilderImpl() {}
//
//		protected BadRequestExceptionBuilderImpl self() {return this;}
//
//		public BadRequestException build() {return new BadRequestException(this);}
//	}
}
