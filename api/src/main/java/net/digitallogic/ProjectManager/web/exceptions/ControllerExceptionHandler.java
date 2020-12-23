package net.digitallogic.ProjectManager.web.exceptions;

import net.digitallogic.ProjectManager.persistence.dto.ErrorDto;
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
						(str1, str2) -> str1+ ", " + str2)
				);

		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(validationErrors)
						.build(),
				HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(BadRequestException.class)
	protected ResponseEntity<Object> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
		return new ResponseEntity<>(
				ErrorDto.builder()
						.message(ex.getMessage())
						.path(request.getRequestURI())
						.build(),
				HttpStatus.BAD_REQUEST
		);
	}
}
