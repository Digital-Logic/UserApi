package net.digitallogic.ProjectManager.web;

import org.springframework.context.MessageSource;

public interface Translator {
	String getTranslatedMessage(MessageSource messageSource);
}
