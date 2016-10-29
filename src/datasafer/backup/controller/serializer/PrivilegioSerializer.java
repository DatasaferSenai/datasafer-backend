package datasafer.backup.controller.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import datasafer.backup.model.Privilegio;

@SuppressWarnings("serial")
public class PrivilegioSerializer extends StdSerializer<Privilegio> {

	public PrivilegioSerializer() {
        this(null);
    }

	public PrivilegioSerializer(Class<Privilegio> t) {
        super(t);
    }

	@Override
	public void serialize(Privilegio value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();
//		jgen.writeNumberField("id", value.id);
//		jgen.writeStringField("itemName", value.itemName);
//		jgen.writeNumberField("owner", value.owner.id);
		jgen.writeEndObject();
	}
}