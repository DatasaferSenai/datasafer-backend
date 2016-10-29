package datasafer.backup.controller.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import datasafer.backup.model.Backup;

@SuppressWarnings("serial")
public class BackupSerializer extends StdSerializer<Backup> {

	public BackupSerializer() {
		this(null);
	}

	public BackupSerializer(Class<Backup> t) {
		super(t);
	}

	@Override
	public void serialize(Backup value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();
//		jgen.writeNumberField("id", value.id);
//		jgen.writeStringField("itemName", value.itemName);
//		jgen.writeNumberField("owner", value.owner.id);
		jgen.writeEndObject();
	}
}