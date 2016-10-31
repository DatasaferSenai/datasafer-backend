package datasafer.backup.controller.serializer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import datasafer.backup.dao.EstacaoDao;
import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;

public class EstacaoSerializer extends StdSerializer<Estacao> {

	@Autowired
	EstacaoDao estacaoDao;

	private static final long serialVersionUID = 1L;

	public EstacaoSerializer() {
		this(null);
	}

	public EstacaoSerializer(Class<Estacao> t) {
		super(t);
	}

	@Override
	public void serialize(Estacao estacao, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();

		jgen.writeStringField("nome", estacao.getNome());
		jgen.writeStringField("descricao", estacao.getDescricao());

		jgen.writeObjectFieldStart("operacoes");
		int contagem_total = 0;
		for (Operacao.Status s : Operacao.Status.values()) {
			int contagem = 0;
			for (Backup b : estacao.getBackups()) {
				for (Operacao o : b.getOperacoes()) {
					if (o.getStatus() == s) {
						contagem++;
					}
				}
			}
			jgen.writeNumberField(s.toString(), contagem);
			contagem_total += contagem;
		}
		jgen.writeNumberField("total", contagem_total);

		jgen.writeEndObject();

		jgen.writeEndObject();
	}
}