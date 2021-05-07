package net.digitallogic.UserApi.web.error.exceptions;

import net.digitallogic.UserApi.web.Translator;
import net.digitallogic.UserApi.web.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnsupportedMediaType extends HttpRequestException {
    public UnsupportedMediaType(ErrorCode errorCode, Translator messageTranslator) {
        super(errorCode, messageTranslator);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    }
}
