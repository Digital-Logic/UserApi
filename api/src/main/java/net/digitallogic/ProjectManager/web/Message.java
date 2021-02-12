package net.digitallogic.ProjectManager.web;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;


public class Message {

	private final String messageCode;
	private final Object[] args;

	protected Message(String messageCode, Object... args) {
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

	public static Message InvalidComparisonOperator(String operator) {
		return new Message("invalid_comparison_operator", operator);
	}

	public static Message ConversionFailure(Object value, Class<?> type) {
		return new Message("arg_conversion_failure", value, type);
	}

	public static Message InvalidFilterProperty(String property) {
		return new Message("invalid_filter_property", property);
	}

	public static Message InvalidFilterQuery() {
		return new Message("invalid_filter_query");
	}

	public static Message DuplicateEntityExist(String type, String id) {
		return new Message("duplicate_entity", type, id);
	}
}
