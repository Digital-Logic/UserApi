package net.digitallogic.UserApi.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Trims whitespace around on json object property values,
 * eg { "key": "  value  " } into { "key": "value" }
 */
@JsonComponent
public class StringTrimDeserializer extends StringDeserializer {

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String value = super.deserialize(p, ctxt);
		return value != null ? value.trim() : null;
	}
}
