package net.digitallogic.ProjectManager.web.error;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.web.MessageTranslator;
import net.digitallogic.ProjectManager.web.error.exceptions.HttpRequestException;
import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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

import javax.persistence.PersistenceException;
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

	/* ***************************************** */
	/* ******   Persistence Exceptions   ******* */
	/* ***************************************** */
	@ExceptionHandler(PropertyValueException.class)
	protected ResponseEntity<ErrorResponse> handlePropertyValueException(PropertyValueException ex, HttpServletRequest request) {
		log.error("Data access exception: {}", ex.getMessage());
		return new ResponseEntity<>(
				new ErrorResponse(
						HttpStatus.INTERNAL_SERVER_ERROR,
						ErrorCode.PROPERTY_VALUE_EXCEPTION,
						MessageTranslator.InternalServerError(),
						messageSource
				),
				HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<ErrorResponse> handleNonTransientDataAccessException(DataIntegrityViolationException ex, HttpServletRequest request) {
		log.error("Data access exception: {}", ex.getMessage());
		return new ResponseEntity<>(
				new ErrorResponse(
						HttpStatus.INTERNAL_SERVER_ERROR,
						ErrorCode.DATA_INTEGRITY_EXCEPTION,
						MessageTranslator.InternalServerError(),
						messageSource
				),
				HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

	@ExceptionHandler(DataAccessException.class)
	protected ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
		log.error("Data access exception: {}", ex.getMessage());
		return new ResponseEntity<>(
			new ErrorResponse(
					HttpStatus.INTERNAL_SERVER_ERROR,
					ErrorCode.DATA_ACCESS_EXCEPTION,
					MessageTranslator.InternalServerError(),
					messageSource
			),
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

	@ExceptionHandler(PersistenceException.class)
	protected ResponseEntity<ErrorResponse> handlePersistenceException(PersistenceException ex, HttpServletRequest request) {
		log.error("Data access exception: {}", ex.getMessage());
		return new ResponseEntity<>(
				new ErrorResponse(
						HttpStatus.INTERNAL_SERVER_ERROR,
						ErrorCode.DAO_PERSISTENCE_EXCEPTION,
						MessageTranslator.InternalServerError(),
						messageSource
				),
				HttpStatus.INTERNAL_SERVER_ERROR
		);
	}
}
