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

	SYSTEM_FAILURE("SysEx"),
	INTERNAL_SERVER_ERROR("InfEx"),

	// Persistence Exception codes
	DATA_INTEGRITY_EXCEPTION("DaInVioEx"),
	DATA_ACCESS_EXCEPTION("daAcEx"),
	DAO_PERSISTENCE_EXCEPTION("daoPrsEx"),
	PROPERTY_VALUE_EXCEPTION("ProValEx")
	;

	@JsonValue
	public final String code;

	private ErrorCode(String code) {
		this.code = code;
	}
}
