package datasafer.backup.controller.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Estacao;

public class EstacaoDeserializer extends StdDeserializer<Estacao> {

	private static final long serialVersionUID = 1L;

	public EstacaoDeserializer() {
		this(null);
	}

	public EstacaoDeserializer(Class<Estacao> t) {
		super(t);
	}

	@Override
	public Estacao deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		Estacao estacao = new Estacao();
		while (jp.nextValue() != JsonToken.END_OBJECT) {
			switch (jp.getCurrentName()) {
				case "nome":
					estacao.setNome(jp.getText());
					break;
				case "descricao":
					estacao.setDescricao(jp.getText());
					break;
			}
		}
		return estacao;
	}
}