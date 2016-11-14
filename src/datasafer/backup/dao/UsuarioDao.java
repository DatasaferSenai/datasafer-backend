package datasafer.backup.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.helper.Registrador;
import datasafer.backup.dao.helper.Validador;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Repository
public class UsuarioDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Usuario obter(String login_usuario) {
		List<Usuario> resultadosUsuario = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario", Usuario.class)
													.setParameter("login_usuario", login_usuario)
													.getResultList();

		return resultadosUsuario.isEmpty() ? null : resultadosUsuario.get(0);
	}

	@Transactional
	public void inserir(String login_solicitante,
						String login_superior,
						Usuario usuario)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		Validador.validar(usuario);

		Usuario solicitante = null;
		if (login_solicitante != null) {
			solicitante = this.obter(login_solicitante);
			if (solicitante == null) {
				throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
			}
		}

		Usuario superior = null;
		if (login_superior != null) {
			superior = this.obter(login_superior);
			if (superior == null) {
				throw new DataRetrievalFailureException("Usuário superior '" + login_superior + "' não encontrado");
			}
		}

		Usuario existente = this.obter(usuario.getLogin());
		if (existente != null) {
			throw new DataIntegrityViolationException("Usuário '" + usuario.getLogin() + "' já existente");
		}

		usuario	.getRegistros()
				.addAll(Registrador.inserir(solicitante, usuario));

		if (superior != null) {
			superior.getColaboradores()
					.add(usuario);
		}
		usuario.setSuperior(superior);

		manager.persist(usuario);
	}

	@Transactional
	public void excluir(String login_solicitante,
						String login_usuario)
			throws DataRetrievalFailureException {

		Usuario solicitante = null;
		if (login_solicitante != null) {
			solicitante = this.obter(login_solicitante);
			if (solicitante == null) {
				throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
			}
		}

		Usuario usuario = this.obter(login_usuario);
		if (usuario == null) {
			throw new DataRetrievalFailureException("Usuário superior '" + login_usuario + "' não encontrado");
		}

		manager.remove(usuario);
	}

	@Transactional
	public void modificar(	String login_solicitante,
							String login_usuario,
							Usuario valores)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		Usuario solicitante = null;
		if (login_solicitante != null) {
			solicitante = this.obter(login_solicitante);
			if (solicitante == null) {
				throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
			}
		}

		Usuario usuario = this.obter(login_usuario);
		if (usuario == null) {
			throw new DataRetrievalFailureException("Usuário superior '" + login_usuario + "' não encontrado");
		}

		if (valores.getLogin() != null && !valores	.getLogin()
													.equals(usuario.getLogin())) {

			Usuario existente = this.obter(valores.getLogin());
			if (existente != null) {
				throw new DataIntegrityViolationException("Usuário '" + valores.getLogin() + "' já existente");
			}

		}

		List<Registro> registros = Registrador.modificar(solicitante, usuario, valores);

		Validador.validar(usuario);

		usuario	.getRegistros()
				.addAll(registros);

		manager.persist(usuario);
	}

	// @Transactional
	public Usuario logar(Usuario usuario) {
		List<Usuario> results = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario AND u.senha = :senha_usuario", Usuario.class)
										.setParameter("login_usuario", usuario.getLogin())
										.setParameter("senha_usuario", usuario.getSenha())
										.getResultList();

		return results.isEmpty() ? null : results.get(0);
	}
}
