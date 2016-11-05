package datasafer.backup.controller.serializer;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;

public class UsuarioSerializer extends StdSerializer<Usuario> {

	private static final long serialVersionUID = 1L;

	public UsuarioSerializer() {
		this(null);
	}

	public UsuarioSerializer(Class<Usuario> t) {
		super(t);
	}

	@Override
	public void serialize(Usuario usuario, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();

		jgen.writeStringField("login", usuario.getLogin());
		jgen.writeStringField("nome", usuario.getNome());
		jgen.writeObjectField("permissoes", usuario.getPermissoes());

		Usuario superior = usuario.getSuperior();
		jgen.writeStringField("superior", superior == null ? null : superior.getLogin());
		jgen.writeObjectField("delegacoes", usuario.getDelegacoes());

		List<Backup> backups = usuario.getBackups();

		long armazenamento_ocupado = 0;
		for (Backup b : backups) {
			Operacao ultimaOperacao = null;
			for (Operacao o : b.getOperacoes()) {
				if (ultimaOperacao == null) {
					ultimaOperacao = o;
				} else {
					if (o	.getData()
							.after(ultimaOperacao.getData())) {
						ultimaOperacao = o;
					}
				}
			}
			if (ultimaOperacao != null) {
				armazenamento_ocupado += ultimaOperacao.getTamanho();
			}
		}

		jgen.writeNumberField("armazenamento", usuario.getArmazenamento());
		jgen.writeNumberField("ocupado", armazenamento_ocupado);

		jgen.writeObjectFieldStart("operacoes");
		for (Backup b : backups) {
			List<Operacao> operacoes = b.getOperacoes();
			for (Operacao.Status s : Operacao.Status.values()) {
				int contagem = 0;
				for (Operacao o : operacoes) {
					if (o.getStatus() == s) {
						contagem++;
					}
				}
				jgen.writeNumberField(s.toString(), contagem);
			}
		}
		int contagem_total = 0;
		for (Backup b : backups) {
			List<Operacao> operacoes = b.getOperacoes();
			if (operacoes != null) {
				contagem_total += operacoes.size();
			}
		}
		jgen.writeNumberField("total", contagem_total);
		jgen.writeEndObject();

		jgen.writeEndObject();
	}
}
