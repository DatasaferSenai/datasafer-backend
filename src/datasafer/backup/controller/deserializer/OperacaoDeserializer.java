package datasafer.backup.controller.deserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Operacao;

public class OperacaoDeserializer extends StdDeserializer<Operacao> {

	private static final long serialVersionUID = 1L;

	public OperacaoDeserializer() {
		this(null);
	}

	public OperacaoDeserializer(Class<Operacao> t) {
		super(t);
	}

	@Override
	public Operacao deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		Operacao operacao = new Operacao();
		while (jp.nextValue() != JsonToken.END_OBJECT) {
			switch (jp.getCurrentName()) {
				case "data":
					try {
						operacao.setData(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(jp.getText()));
					} catch (ParseException e) {
						throw new IOException("Formato da data inválido");
					}
					break;
				case "status":
					operacao.setStatus(Operacao.Status.valueOf(jp.getText()));
					break;
				case "tamanho":
					operacao.setTamanho(jp.getLongValue());
					break;
			}
		}
		return operacao;
	}
}