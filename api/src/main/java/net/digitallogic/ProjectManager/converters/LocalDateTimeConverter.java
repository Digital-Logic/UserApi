package net.digitallogic.ProjectManager.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

	@Override
	public LocalDateTime convert(@NonNull String source) {
		return LocalDateTime.parse(source);
	}
}
