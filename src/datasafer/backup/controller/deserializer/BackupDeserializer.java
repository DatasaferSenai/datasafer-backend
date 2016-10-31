package datasafer.backup.controller.deserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Backup;

public class BackupDeserializer extends StdDeserializer<Backup> {

	private static final long serialVersionUID = 1L;

	public BackupDeserializer() {
		this(null);
	}

	public BackupDeserializer(Class<Backup> t) {
		super(t);
	}

	@Override
	public Backup deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		Backup backup = new Backup();
		while (jp.nextValue() != JsonToken.END_OBJECT) {
			switch (jp.getCurrentName()) {
				case "nome":
					backup.setNome(jp.getText());
					break;
				case "descricao":
					backup.setDescricao(jp.getText());
					break;
				case "pasta":
					backup.setPasta(jp.getText());
					break;
				case "intervalo":
					backup.setIntervalo(jp.getIntValue());
					break;
				case "inicio":
					try {
						backup.setInicio(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(jp.getText()));
					} catch (ParseException e) {
						throw new IOException("Formato da data inválido");
					}
					break;
			}
		}
		return backup;
	}

}