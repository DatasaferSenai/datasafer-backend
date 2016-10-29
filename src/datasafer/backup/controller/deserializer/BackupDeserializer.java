package datasafer.backup.controller.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Backup;

@SuppressWarnings("serial")
public class BackupDeserializer extends StdDeserializer<Backup> {

	public BackupDeserializer() {
		this(null);
	}

	public BackupDeserializer(Class<Backup> t) {
		super(t);
	}

	@Override
	public Backup deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		return null;
	}

}