package datasafer.backup.controller.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Privilegio;

@SuppressWarnings("serial")
public class PrivilegioDeserializer extends StdDeserializer<Privilegio> {

	public PrivilegioDeserializer() {
		this(null);
	}

	public PrivilegioDeserializer(Class<Privilegio> t) {
		super(t);
	}

	@Override
	public Privilegio deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		return null;
	}
}