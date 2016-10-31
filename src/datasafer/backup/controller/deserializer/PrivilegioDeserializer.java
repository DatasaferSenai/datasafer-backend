package datasafer.backup.controller.deserializer;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Privilegio;

public class PrivilegioDeserializer extends StdDeserializer<Privilegio> {

	private static final long serialVersionUID = 1L;

	public PrivilegioDeserializer() {
		this(null);
	}

	public PrivilegioDeserializer(Class<Privilegio> t) {
		super(t);
	}

	@Override
	public Privilegio deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		Privilegio privilegio = new Privilegio();
		while (jp.nextValue() != JsonToken.END_OBJECT) {
			switch (jp.getCurrentName()) {
				case "nome":
					privilegio.setNome(jp.getText());
					break;
				case "permissoes":
					@SuppressWarnings("unchecked")
					Set<Privilegio.Permissao> permissoes = jp.readValueAs(Set.class);
					privilegio.setPermissoes(permissoes);
					break;
			}
		}
		return privilegio;

	}
}