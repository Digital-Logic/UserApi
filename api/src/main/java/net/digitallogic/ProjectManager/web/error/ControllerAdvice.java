package net.digitallogic.ProjectManager.web.error;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.exceptions.HttpRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

	private final MessageSource messageSource;

	@Autowired
	public ControllerAdvice(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {

		List<ErrorResponse.ErrorBlock> errorBlocks = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.filter(field -> field.getDefaultMessage() != null) // Filter null messages
				.map(field -> new ErrorResponse.ErrorBlock(
						field.getField(),
						ErrorCode.VALIDATION_FAILED,
						field.getDefaultMessage())
				)
				.collect(Collectors.toList());

		return new ResponseEntity<>(
				new ErrorResponse(HttpStatus.BAD_REQUEST,
						ErrorCode.VALIDATION_FAILED,
						null,
						errorBlocks),
				headers,
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(HttpRequestException.class)
	protected ResponseEntity<ErrorResponse> handleHttpRequestException(HttpRequestException ex) {
		log.info("Handling HttpRequestException.");
		return new ResponseEntity<>(
				new ErrorResponse(ex, messageSource),
				ex.getHttpStatus()
		);
	}

	@ExceptionHandler(PropertyReferenceException.class)
	protected ResponseEntity<ErrorResponse> handlePropertyReferenceException(
			PropertyReferenceException ex, HttpServletRequest request) {
		return new ResponseEntity<>(
				new ErrorResponse(
						HttpStatus.BAD_REQUEST,
						ErrorCode.INVALID_PROPERTY,
						MessageTranslator.InvalidProperty(),
						messageSource
				),
				HttpStatus.BAD_REQUEST
		);
	}

	// TODO change message output
	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	protected ResponseEntity<ErrorResponse> handleOptimisticLockException(
			ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
		log.error("OptimisticLockException thrown on request: {}, Entity: {}",
				request.getRequestURI(), ex.getMessage());

		return new ResponseEntity<>(
				new ErrorResponse(
						HttpStatus.CONFLICT,
						ErrorCode.CONFLICT,
						MessageTranslator.ConflictDataFailure(),
						messageSource
				),
				HttpStatus.CONFLICT
		);
	}
}
