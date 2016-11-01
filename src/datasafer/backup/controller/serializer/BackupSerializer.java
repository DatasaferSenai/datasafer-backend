package datasafer.backup.controller.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;

@Service
public class BackupSerializer extends StdSerializer<Backup> {

	private static final long serialVersionUID = 1L;

	public BackupSerializer() {
		this(null);
	}

	public BackupSerializer(Class<Backup> t) {
		super(t);
	}

	@Override
	public void serialize(Backup backup, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();

		jgen.writeStringField("nome", backup.getNome());
		jgen.writeStringField("descricao", backup.getDescricao());
		jgen.writeStringField("pasta", backup.getPasta());
		jgen.writeNumberField("intervalo", backup.getIntervalo());
		jgen.writeStringField("inicio", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(backup.getInicio()));

		jgen.writeObjectFieldStart("operacoes");
		int contagem_total = 0;
		for (Operacao.Status s : Operacao.Status.values()) {
			int contagem = 0;
			for (Operacao o : backup.getOperacoes()) {
				contagem_total++;
				if (o.getStatus() == s) {
					contagem++;
				}
			}
			jgen.writeNumberField(s.toString(), contagem);
		}
		jgen.writeNumberField("total", contagem_total);
		
		jgen.writeEndObject();

		jgen.writeEndObject();
	}
}