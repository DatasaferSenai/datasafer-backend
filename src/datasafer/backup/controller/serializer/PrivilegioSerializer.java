package datasafer.backup.controller.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import datasafer.backup.model.Privilegio;

public class PrivilegioSerializer extends StdSerializer<Privilegio> {

	private static final long serialVersionUID = 1L;

	public PrivilegioSerializer() {
		this(null);
	}

	public PrivilegioSerializer(Class<Privilegio> t) {
		super(t);
	}

	@Override
	public void serialize(Privilegio privilegio, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();

		jgen.writeStringField("nome", privilegio.getNome());
		jgen.writeObjectField("permissoes", privilegio.getPermissoes());

		jgen.writeEndObject();
	}
}