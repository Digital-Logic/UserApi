package net.digitallogic.ProjectManager.web.error.exceptions;

import lombok.extern.slf4j.Slf4j;
import net.digitallogic.ProjectManager.web.Translator;
import net.digitallogic.ProjectManager.web.error.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InternalSystemFailure extends HttpRequestException {

    public InternalSystemFailure(ErrorCode errorCode, Translator messageTranslator, List<ErrorMessage> errorMessages) {
        super(errorCode, messageTranslator, errorMessages);
    }

    public InternalSystemFailure(ErrorCode errorCode, Translator messageTranslator) {
        super(errorCode, messageTranslator);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
