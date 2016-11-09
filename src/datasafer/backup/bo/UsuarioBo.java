package datasafer.backup.bo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import datasafer.backup.bo.helper.Modificador;
import datasafer.backup.bo.helper.Validador;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Usuario;

@Service
public class UsuarioBo {

	@Autowired
	private UsuarioDao usuarioDao;

	public Usuario obter(String login_usuario) throws DataRetrievalFailureException {

		Usuario usuario = usuarioDao.obter(login_usuario);
		if (usuario == null) {
			throw new DataRetrievalFailureException("Usuário não encontrado");
		}

		return usuario;
	}

	public void inserir(String login_solicitante,
						String login_superior,
						Usuario usuario)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		if (usuarioDao.obter(login_solicitante) == null) {
			throw new DataRetrievalFailureException("Usuário solicitante não encontrado");
		}

		if (usuarioDao.obter(login_superior) == null) {
			throw new DataRetrievalFailureException("Usuário superior não encontrado");
		}

		Validador.validar(usuario);

		if (usuarioDao.obter(usuario.getLogin()) != null) {
			throw new DataIntegrityViolationException("Estação já existente");
		}

		usuarioDao.inserir(login_solicitante, login_superior, usuario);
	}

	public void modificar(	String login_solicitante,
							String login_usuario,
							Map<String, Object> valores)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		if (usuarioDao.obter(login_solicitante) == null) {
			throw new DataRetrievalFailureException("Usuário solicitante não encontrado");
		}

		Usuario usuario = usuarioDao.obter(login_usuario);
		if (usuario == null) {
			throw new DataRetrievalFailureException("Usuário não encontrado");
		}

		Modificador.modificar(usuario, valores);
		Validador.validar(usuario);

		usuarioDao.modificar(login_solicitante, usuario);
	}

	public void modificar(	String login_solicitante,
							Usuario usuario)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		if (usuarioDao.obter(login_solicitante) == null) {
			throw new DataRetrievalFailureException("Usuário solicitante não encontrado");
		}

		Validador.validar(usuario);

		usuarioDao.modificar(login_solicitante, usuario);
	}

	public Usuario logar(Usuario usuario_verificar) throws DataRetrievalFailureException {

		Usuario usuario_existente = usuarioDao.obter(usuario_verificar.getLogin());
		if (usuario_existente == null) {
			throw new DataRetrievalFailureException("Usuário não encontrado");
		}

		if (!usuario_existente	.getSenha()
								.equals(usuario_verificar.getSenha())) {
			throw new DataRetrievalFailureException("Usuário ou senha inválidos");
		}

		return usuario_existente;
	}

}
