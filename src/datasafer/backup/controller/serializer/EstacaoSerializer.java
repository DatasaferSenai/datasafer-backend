package datasafer.backup.controller.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import datasafer.backup.model.Estacao;

@SuppressWarnings("serial")
public class EstacaoSerializer extends StdSerializer<Estacao> {

	public EstacaoSerializer() {
		this(null);
	}

	public EstacaoSerializer(Class<Estacao> t) {
		super(t);
	}

	@Override
	public void serialize(Estacao value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();
//		jgen.writeNumberField("id", value.id);
//		jgen.writeStringField("itemName", value.itemName);
//		jgen.writeNumberField("owner", value.owner.id);
		jgen.writeEndObject();
	}
}