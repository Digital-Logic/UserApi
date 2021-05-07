package net.digitallogic.UserApi.web;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

public class MessageTranslator implements Translator {
	private final String messageCode;
	private final Object[] args;

	private MessageTranslator(String messageCode, Object... args) {
		this.messageCode = messageCode;
		this.args = args;
	}

	@Override
	public String getTranslatedMessage(MessageSource messageSource) {
		return messageSource.getMessage(
				messageCode,
				args,
				LocaleContextHolder.getLocale()
		);
	}


	public static MessageTranslator ConversionFailed(Class<?> type, @Nullable Object value) {
		return new MessageTranslator("conversion_failed_2", value, type);
	}

	public static MessageTranslator ConversionFailed() {
		return new MessageTranslator( "conversion_failed_0");
	}

	public static MessageTranslator InvalidComparisonOperator(String operator) {
		return new MessageTranslator( "invalid_comparison_operator", operator);
	}

	public static MessageTranslator InvalidProperty() {
		return new MessageTranslator("invalid_property");
	}

	public static MessageTranslator InvalidFilterProperty(String property) {
		return new MessageTranslator( "invalid_filter_property", property);
	}

	public static MessageTranslator InvalidFilterQuery() {
		return new MessageTranslator( "invalid_filter_query");
	}

	public static MessageTranslator InvalidExpansionProperty() {
		return new MessageTranslator( "invalid_expansion_property");
	}
	public static MessageTranslator InvalidJson() {
		return new MessageTranslator("invalid_json");
	}

	// Entity

	public static MessageTranslator DuplicateEntityExist(String type, Object id) {
		return new MessageTranslator( "duplicate_entity", type, id);
	}

	public static MessageTranslator NonExistentEntity(String type, Object id) {
		return new MessageTranslator( "non_existent_entity", type, id);
	}

	public static MessageTranslator ConflictDataFailure() {
		return new MessageTranslator("conflict_data");
	}

	// Auth
	public static MessageTranslator AccountDisabled() {
		return new MessageTranslator("AccountDisabled",
				"AbstractUserDetailsAuthenticationProvider.disabled");
	}
	public static MessageTranslator AccountLocked() {
		return new MessageTranslator("AbstractUserDetailsAuthenticationProvider.locked");
	}
	public static MessageTranslator AccountExpired() {
		return new MessageTranslator("AbstractUserDetailsAuthenticationProvider.expired");
	}
	public static MessageTranslator CredentialsExpired() {
		return new MessageTranslator("AbstractUserDetailsAuthenticationProvider.credentialsExpired");
	}
	public static MessageTranslator BadCredentials() {
		return new MessageTranslator("AbstractUserDetailsAuthenticationProvider.badCredentials");
	}
	public static MessageTranslator InternalServerError() {
		return new MessageTranslator("internal_server_error");
	}

	public static MessageTranslator invalidAccountActivationToken() { return new MessageTranslator("invalidAccountActivationToken"); }

	// Token Messages
	public static MessageTranslator TokenIsInvalid() {
		return new MessageTranslator("token.invalid");
	}
	public static MessageTranslator TokenIsExpired() { return new MessageTranslator("token.expired"); }
	public static MessageTranslator TokenIsUsed() { return new MessageTranslator("token.used"); }

	// Account  Activation
	public static MessageTranslator AccountAlreadyEnabled() { return new MessageTranslator("accountActivation.accountEnabled"); }

	// Reset password
	public static MessageTranslator ResetPasswordAccountDisabled() { return new MessageTranslator("resetPassword.accountDisabled");}
	public static MessageTranslator ResetPasswordAccountLocked() { return new MessageTranslator("resetPassword.accountLock");}
	public static MessageTranslator ResetPasswordAccountExpired() { return new MessageTranslator("resetPassword.accountExpired");}
	public static MessageTranslator ResetPasswordCredentialsExpired() { return new MessageTranslator("resetPassword.credentialsExpired");}
}
