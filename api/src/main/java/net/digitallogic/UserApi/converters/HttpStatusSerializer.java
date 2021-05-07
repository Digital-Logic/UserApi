package net.digitallogic.UserApi.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@JsonComponent
public class HttpStatusSerializer extends StdSerializer<HttpStatus> {

	public HttpStatusSerializer() {
		this(null);
	}
	public HttpStatusSerializer(Class<HttpStatus> t) {
		super(t);
	}

	@Override
	public void serialize(HttpStatus value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeNumber(value.value());
	}
}
