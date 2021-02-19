package net.digitallogic.ProjectManager.web.error;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {

	CONVERSION_FAILED("ConversionFailed"),
	OPERATOR_NOT_SUPPORTED("OperatorNotSupported"),
	INVALID_PROPERTY("InvalidProperty"),
	INVALID_FILTER_QUERY("InvalidFilterQuery"),
	INVALID_EXPANSION_PROPERTY("InvalidExpansionProperty"),

	// Entity
	DUPLICATE_ENTITY("DuplicateEntity"),
	NON_EXISTENT_ENTITY("NonExistentEntity"),
	CONFLICT("Conflict"),

	// Auth
	ACCOUNT_DISABLED("AccountDisabled"),
	ACCOUNT_LOCKED("AccountLocked"),
	ACCOUNT_EXPIRED("AccountExpired"),
	CREDENTIALS_EXPIRED("CredentialsExpired"),
	BAD_CREDENTIALS("BadCredentials"),
	VALIDATION_FAILED("ValidationFailed"),
	AUTHENTICATION_FAILURE("AuthenticationFailed"),

	INVALID_TOKEN("InvalidToken"),

	SYSTEM_FAILURE("SystemFailure")
	;

	@JsonValue
	public final String code;

	private ErrorCode(String code) {
		this.code = code;
	}
}
