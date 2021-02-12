package net.digitallogic.ProjectManager.web.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("${server.error.path}")
@Slf4j
public class RestErrorController extends AbstractErrorController {

	protected final String errorPath;
	protected final ErrorAttributes errorAttributes;

	@Autowired
	public RestErrorController(
			@Value("${server.error.path}") String errorPath,
			ErrorAttributes errorAttributes) {
		super(errorAttributes, Collections.emptyList());
		this.errorPath = errorPath;
		this.errorAttributes = errorAttributes;
	}

	@RequestMapping
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request));

		return new ResponseEntity<>(body, this.getStatus(request) );
	}

	@Override
	public String getErrorPath() {
		return errorPath;
	}

	protected ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request) {
		ErrorAttributeOptions options = ErrorAttributeOptions.defaults();

		options = options.including(ErrorAttributeOptions.Include.MESSAGE);

		return options;
	}
}
