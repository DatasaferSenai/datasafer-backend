package datasafer.backup.controller.deserializer;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Permissao;

public class UsuarioDeserializer extends StdDeserializer<Usuario> {

	private static final long serialVersionUID = 1L;

	public UsuarioDeserializer() {
		this(null);
	}

	public UsuarioDeserializer(Class<Usuario> t) {
		super(t);
	}

	@Override
	public Usuario deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		Usuario usuario = new Usuario();
		while (jp.nextValue() != JsonToken.END_OBJECT) {
			switch (jp.getCurrentName()) {
				case "nome":
					usuario.setNome(jp.getText());
					break;
				case "permissoes":
					@SuppressWarnings("unchecked")
					Set<Permissao> permissoes = (Set<Permissao>) jp.getEmbeddedObject();
					usuario.setPermissoes(permissoes);
					break;
				case "email":
					usuario.setEmail(jp.getText());
					break;
				case "login":
					usuario.setLogin(jp.getText());
					break;
				case "senha":
					usuario.setSenha(jp.getText());
					break;
				case "armazenamento":
					usuario.setArmazenamento(jp.getLongValue());
					break;
			}
		}
		return usuario;
	}
}
