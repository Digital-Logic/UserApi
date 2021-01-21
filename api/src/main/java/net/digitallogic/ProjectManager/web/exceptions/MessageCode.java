package net.digitallogic.ProjectManager.web.exceptions;

public enum MessageCode {

	FILTER_INVALID_COMPARISON_OPERATOR("exception.filter.invalidComparisonOperator"),
	FILTER_ARG_CONVERSION("exception.filter.ArgConversion"),
	FILTER_INVALID_PROPERTY("exception.filter.invalidFilterProperty"),
	FILTER_CONVERSION_ERROR("exception.conversionError"),
	INVALID_EXPANSION_PROPERTY("exception.invalidExpansionProperty"),

	TYPE_CONVERSION_ERROR("exception.type.conversionError"),

	DUPLICATE_ENTITY("exception.duplicateEntity"),
	ENTITY_DOES_NOT_EXIST("exception.entityDoesNotExist")
	;

	public final String property;

	MessageCode(String property) {
		this.property = property;
	}
}
