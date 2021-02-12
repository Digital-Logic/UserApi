package net.digitallogic.ProjectManager.web.error;

public enum ErrorCode {

	TYPE_CONVERSION("TypeConversion"),
	OPERATOR_NOT_SUPPORTED("OperatorNotSupported"),
	INVALID_QUERY("InvalidQuery"),
	INVALID_PROPERTY("InvalidProperty"),
	VALIDATION_FAILURE("ValidationFailure"),
	DUPLICATE_ENTITY("DuplicateEntity"),
	NOT_FOUND("NotFound"),
	ACCOUNT_ACTIVATION_ERROR("AccountActivationError"),
	ACCOUNT_LOCKED_ERROR("AccountLockedError"),
	ACCOUNT_EXPIRED_ERROR("AccountExpiredError"),
	CREDENTIALS_EXPIRED("CREDENTIALS_EXPIRED"),
	BAD_CREDENTIALS("BAD_CREDENTIALS")

	;

	public final String code;
	private ErrorCode(String code) {
		this.code = code;
	}
}
