package net.digitallogic.ProjectManager.web.exceptions;

import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class InvalidExpansionProperties extends RuntimeException {

	@Getter
	private final List<PropertyError> errors;

	public InvalidExpansionProperties(List<PropertyError> errors) {
		super("Invalid expansion properties.");
		this.errors = errors;
	}

	public static class PropertyError {

		@Getter
		private final String property;

		@Getter
		private final String error;

		private PropertyError(String property, String error) {
			this.property = property;
			this.error = error;
		}

		public static PropertyError of(@NonNull String property, @NonNull String error) {
			requireNonNull(property);
			requireNonNull(error);

			return new PropertyError(property, error);
		}
	}
}
