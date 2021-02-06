package net.digitallogic.ProjectManager.web.controller;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.persistence.dto.ErrorDto;
import net.digitallogic.ProjectManager.web.exceptions.HttpRequestException;
import net.digitallogic.ProjectManager.web.exceptions.MessageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	private final MessageSource messageSource;

	@Autowired
	public ExceptionHandlerController(MessageSource messageSource) {this.messageSource = messageSource;}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {

		Map<String, String> validationErrors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.filter(field -> field.getDefaultMessage() != null) // Filter null messages
				.collect(Collectors.toMap(
						FieldError::getField,
						FieldError::getDefaultMessage,
						(str1, str2) -> str1 + ", " + str2)
				);

		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(validationErrors)
						.code(MessageCode.FIELD_VALIDATION_ERROR.code)
						.build(),
				headers,
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(HttpRequestException.class)
	protected ResponseEntity<ErrorDto> handleHttpRequestException(HttpRequestException ex, HttpServletRequest request) {
		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(ex.getMessage(messageSource))
						.code(ex.getStatusCode())
						.details(ex.getDetails())
						.path(request.getRequestURI())
				.build(),
				ex.getHttpStatus()
		);
	}

	@ExceptionHandler(PropertyReferenceException.class)
	protected ResponseEntity<ErrorDto> handlePropertyReferenceException(
			PropertyReferenceException ex, HttpServletRequest request) {
		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(getMessage(MessageCode.ENTITY_INVALID_PROPERTY.message, ex.getPropertyName()))
						.code(MessageCode.ENTITY_INVALID_PROPERTY.code)
						.path(request.getRequestURI())
						.build(),
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(CookieTheftException.class)
	protected ResponseEntity<ErrorDto> handleCookieTheftException() {
		return new ResponseEntity<>(
				ErrorDto.builder().build(),
				HttpStatus.UNAUTHORIZED
		);
	}

	// TODO change message output
	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	protected ResponseEntity<ErrorDto> handleOptimisticLockException(
			ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
		log.error("OptimisticLockException thrown on request: {}, Entity: {}",
				request.getRequestURI(), ex.getMessage());

		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(ex.getMessage())
						.path(request.getRequestURI())
						.build(),
				HttpStatus.CONFLICT
		);
	}

	private String getMessage(String messageCode, Object... args) {
		return messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale());
	}
}
