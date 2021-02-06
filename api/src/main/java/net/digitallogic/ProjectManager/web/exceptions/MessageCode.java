package net.digitallogic.ProjectManager.web.exceptions;

public enum MessageCode {

	FIELD_VALIDATION_ERROR("field validation error", "req-035"),

	FILTER_INVALID_COMPARISON_OPERATOR("exception.filter.invalidComparisonOperator", "req-001"),
	FILTER_ARG_CONVERSION("exception.filter.ArgConversion", "req-005"),
	FILTER_INVALID_PROPERTY("exception.filter.invalidFilterProperty", "req-0010"),
	FILTER_CONVERSION_ERROR("exception.conversionError", "req-015"),
	FILTER_INVALID_QUERY("exception.filter.invalidQuery", "req-020"),
	INVALID_EXPANSION_PROPERTY("exception.invalidExpansionProperty", "req-0025"),

	TYPE_CONVERSION_ERROR("exception.type.conversionError", "req-030"),

	DUPLICATE_ENTITY("exception.duplicateEntity", "req-100"),
	ENTITY_DOES_NOT_EXIST("exception.entity.doesNotExist", "req-110"),
	ENTITY_INVALID_PROPERTY("exception.entity.invalidProperty", "req-115"),

	AUTH_ACCOUNT_NOT_ACTIVATED("AbstractUserDetailsAuthenticationProvider.disabled", "auth-005"),
	AUTH_ACCOUNT_LOCKED("AbstractUserDetailsAuthenticationProvider.locked", "auth-010"),
	AUTH_ACCOUNT_EXPIRED("AbstractUserDetailsAuthenticationProvider.expire", "auth-015"),
	AUTH_CREDENTIALS_EXPIRED("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "auth-020"),
	AUTH_BAD_CREDENTIALS("AbstractUserDetailsAuthenticationProvider.badCredentials", "auth-025")
	;

	public final String message;
	public final String code;

	MessageCode(String message, String code) {
		this.message = message;
		this.code = code;
	}
}
