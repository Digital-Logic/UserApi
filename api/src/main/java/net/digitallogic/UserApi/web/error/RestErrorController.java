package net.digitallogic.UserApi.web.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(path = "${server.error.path}")
@Slf4j
public class RestErrorController extends AbstractErrorController {

	protected final String errorPath;
	protected final ErrorAttributes errorAttributes;
	protected final ErrorProperties.IncludeStacktrace includeStackTrace;

	@Autowired
	public RestErrorController(
			@Value("${server.error.path}") String errorPath,
			@Value("${server.error.include-stacktrace}") ErrorProperties.IncludeStacktrace includeStackTrace,
			ErrorAttributes errorAttributes) {
		super(errorAttributes, Collections.emptyList());
		this.errorPath = errorPath;
		this.includeStackTrace = includeStackTrace;
		this.errorAttributes = errorAttributes;
	}

	@RequestMapping
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		ErrorAttributeOptions options = ErrorAttributeOptions.defaults()
			.including(ErrorAttributeOptions.Include.MESSAGE)
			.including(ErrorAttributeOptions.Include.EXCEPTION);
		if(includeStackTrace == ErrorProperties.IncludeStacktrace.ALWAYS)
			options.including(ErrorAttributeOptions.Include.STACK_TRACE);

		Map<String, Object> body = getErrorAttributes(request, options);
		log.error("Unhandled exception: {}", body);

		return new ResponseEntity<>(body, this.getStatus(request));
	}

	@Override
	@Deprecated
	public String getErrorPath() {
		return errorPath;
	}

}
