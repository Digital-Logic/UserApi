package net.digitallogic.ProjectManager.web.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public class RestErrorAttributes extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
		Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, options);
		ErrorResponse errorResponse = ErrorResponse.fromDefaultAttributeMap(defaultErrorAttributes);
		return errorResponse.toAttributeMap();
	}
}
