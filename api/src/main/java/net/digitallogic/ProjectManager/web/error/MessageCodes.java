package net.digitallogic.ProjectManager.web.error;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public enum MessageCodes {

	FILTER_INVALID_COMPARISON_OPERATOR_1("exception.filter.invalidComparisonOperator"), ///
	FILTER_ARG_CONVERSION_2("exception.filter.ArgConversion"), ///

	FILTER_INVALID_PROPERTY_1("exception.filter.invalidFilterProperty"), ////
	FILTER_CONVERSION_ERROR("exception.conversionError"), ///
	FILTER_INVALID_QUERY_0("exception.filter.invalidQuery"), ////
	INVALID_EXPANSION_PROPERTY_0("exception.invalidExpansionProperty"),
	INVALID_PROPERTY_1("exception.invalidProperty"),

	TYPE_CONVERSION_ERROR_0("exception.type.conversionError"),

	DUPLICATE_ENTITY_2("exception.duplicateEntity"), ///
	ENTITY_NOT_FOUND("exception.entity.doesNotExist"),
	ENTITY_INVALID_PROPERTY("exception.entity.invalidProperty"), // TODO REMOVE THIS

	AUTH_ACCOUNT_NOT_ACTIVATED("AbstractUserDetailsAuthenticationProvider.disabled"),
	AUTH_ACCOUNT_LOCKED("AbstractUserDetailsAuthenticationProvider.locked"),
	AUTH_ACCOUNT_EXPIRED("AbstractUserDetailsAuthenticationProvider.expire"),
	AUTH_CREDENTIALS_EXPIRED("AbstractUserDetailsAuthenticationProvider.credentialsExpired"),
	AUTH_BAD_CREDENTIALS("AbstractUserDetailsAuthenticationProvider.badCredentials")
	;

	public String getMessage(MessageSource messageSource, Object... args) {
		return messageSource.getMessage(message, args, LocaleContextHolder.getLocale());
	}

	public final String message;

	MessageCodes(String message) {
		this.message = message;
	}
}
