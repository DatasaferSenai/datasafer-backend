package datasafer.backup.controller.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Estacao;

@SuppressWarnings("serial")
public class EstacaoDeserializer extends StdDeserializer<Estacao> {

	public EstacaoDeserializer() {
		this(null);
	}

	public EstacaoDeserializer(Class<Estacao> t) {
		super(t);
	}

	@Override
	public Estacao deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		return null;
	}
}