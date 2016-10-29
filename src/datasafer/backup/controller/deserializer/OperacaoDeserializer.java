package datasafer.backup.controller.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Operacao;

@SuppressWarnings("serial")
public class OperacaoDeserializer extends StdDeserializer<Operacao> {

	public OperacaoDeserializer() {
		this(null);
	}

	public OperacaoDeserializer(Class<Operacao> t) {
		super(t);
	}

	@Override
	public Operacao deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		return null;
	}
}