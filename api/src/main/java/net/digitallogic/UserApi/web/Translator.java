package net.digitallogic.UserApi.web;

import org.springframework.context.MessageSource;

public interface Translator {
	String getTranslatedMessage(MessageSource messageSource);
}
