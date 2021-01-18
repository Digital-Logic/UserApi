package net.digitallogic.ProjectManager.web.exceptions;

import net.digitallogic.ProjectManager.persistence.dto.ErrorDto;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

import static net.digitallogic.ProjectManager.web.exceptions.InvalidExpansionProperties.*;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {


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
						.build(),
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(BadRequestException.class)
	protected ResponseEntity<ErrorDto<Object>> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(ex.getMessage())
						.path(request.getRequestURI())
						.build(),
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(InvalidExpansionProperties.class)
	protected ResponseEntity<ErrorDto<Object>> handleInvalidExpansionProperty(InvalidExpansionProperties ex, HttpServletRequest request) {

		Map<String, String> propertyErrors = ex.getErrors().stream()
				.collect(Collectors
						.toUnmodifiableMap(
								PropertyError::getProperty,
								PropertyError::getError
						));

		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(propertyErrors)
						.path(request.getRequestURI())
						.build(),
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(PropertyReferenceException.class)
	protected ResponseEntity<ErrorDto<Object>> handlePropertyReferenceException(PropertyReferenceException ex, HttpServletRequest request) {
		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(ex.getMessage())
						.path(request.getRequestURI())
						.build(),
				HttpStatus.BAD_REQUEST
		);
	}
}
