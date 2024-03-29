package net.digitallogic.UserApi.web.error;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {

	NOT_APPLICABLE("NtApp"),
	CONVERSION_FAILED("ConversionFailed"),
	OPERATOR_NOT_SUPPORTED("OperatorNotSupported"),
	INVALID_PROPERTY("InvalidProperty"),
	INVALID_FILTER_QUERY("InvalidFilterQuery"),
	INVALID_EXPANSION_PROPERTY("InvalidExpansionProperty"),
	INVALID_JSON_DATA("InvalidJson"),
	UNSUPPORTED_MEDIA_TYPE("UnSupportedType"),
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

	// Token Error codes
	TOKEN_INVALID("tknInv"),
	TOKEN_EXPIRED("TknExp"),
	TOKEN_USED("TknUsd"),

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

	ErrorCode(String code) {
		this.code = code;
	}
}
