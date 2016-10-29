package datasafer.backup.controller.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Usuario;

@SuppressWarnings("serial")
public class UsuarioDeserializer extends StdDeserializer<Usuario> {

	public UsuarioDeserializer() {
		this(null);
	}

	public UsuarioDeserializer(Class<Usuario> t) {
		super(t);
	}

	@Override
	public Usuario deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		return null;
	}
}
