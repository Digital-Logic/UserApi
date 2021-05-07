package net.digitallogic.UserApi.web.error.exceptions;

import net.digitallogic.UserApi.web.Translator;
import net.digitallogic.UserApi.web.error.ErrorCode;
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
