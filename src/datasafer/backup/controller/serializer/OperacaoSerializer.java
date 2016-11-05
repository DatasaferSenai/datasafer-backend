package datasafer.backup.controller.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import datasafer.backup.model.Operacao;

public class OperacaoSerializer extends StdSerializer<Operacao> {

	private static final long serialVersionUID = 1L;

	public OperacaoSerializer() {
		this(null);
	}

	public OperacaoSerializer(Class<Operacao> t) {
		super(t);
	}

	@Override
	public void serialize(Operacao operacao, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();

		jgen.writeStringField("data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(operacao.getData()));
		jgen.writeStringField("status", operacao.getStatus());
		jgen.writeNumberField("tamanho", operacao.getTamanho());

		jgen.writeEndObject();
	}
}