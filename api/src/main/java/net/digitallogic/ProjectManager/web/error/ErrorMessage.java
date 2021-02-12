package net.digitallogic.ProjectManager.web.error;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

public class ErrorMessage  {
	private final String errorCode;
	private final String messageCode;
	private final Object[] args;

	private ErrorMessage(String errorCode, String messageCode, Object... args) {
		this.errorCode = errorCode;
		this.messageCode = messageCode;
		this.args = args;
	}

	public String getMessage(MessageSource messageSource) {
		return messageSource.getMessage(
				messageCode,
				args,
				LocaleContextHolder.getLocale()
		);
	}

	public String getErrorCode() {
		return errorCode;
	}


	public static ErrorMessage ConversionFailed(Class<?> type, @Nullable Object value) {
		return new ErrorMessage("ConversionFailed", "conversion_failed_2", value, type);
	}

	public static ErrorMessage ConversionFailed() {
		return new ErrorMessage("ConversionFailed", "conversion_failed_0");
	}

	public static ErrorMessage InvalidComparisonOperator(String operator) {
		return new ErrorMessage("OperatorNotSuppored", "invalid_comparison_operator", operator);
	}

	public static ErrorMessage InvalidFilterProperty(String property) {
		return new ErrorMessage("InvalidProperty", "invalid_filter_property", property);
	}

	public static ErrorMessage InvalidFilterQuery() {
		return new ErrorMessage("InvalidFilterQuery", "invalid_filter_query");
	}

	public static ErrorMessage InvalidExpansionProperty() {
		return new ErrorMessage("InvalidExpansionProperty", "invalid_expansion_property");
	}

	public static ErrorMessage DuplicateEntityExist(String type, Object id) {
		return new ErrorMessage("DuplicateEntity", "duplicate_entity", type, id);
	}

	public static ErrorMessage NonExistentEntity(String type, Object id) {
		return new ErrorMessage("NonExistentEntity", "non_existent_entity", type, id);
	}

	public static ErrorMessage AccountDisabled() {
		return new ErrorMessage("AccountDisabled",
				"AbstractUserDetailsAuthenticationProvider.disabled");
	}
	public static ErrorMessage AccountLocked() {
		return new ErrorMessage("AccountLocked",
				"AbstractUserDetailsAuthenticationProvider.locked");
	}
	public static ErrorMessage AccountExpired() {
		return new ErrorMessage("AccountExpired",
				"AbstractUserDetailsAuthenticationProvider.expired");
	}
	public static ErrorMessage CredentialsExpired() {
		return new ErrorMessage("CredentialsExpired",
				"AbstractUserDetailsAuthenticationProvider.credentialsExpired");
	}
	public static ErrorMessage BadCredentials() {
		return new ErrorMessage("BadCredentials",
				"AbstractUserDetailsAuthenticationProvider.badCredentials");
	}
}
