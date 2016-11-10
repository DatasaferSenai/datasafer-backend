package datasafer.backup.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.helper.Modificador;
import datasafer.backup.dao.helper.Validador;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Registro.Tipo;
import datasafer.backup.model.Usuario;
import datasafer.backup.model.Usuario.Status;

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
						Usuario novo)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		Validador.validar(novo);

		List<Usuario> resultadosSolicitante = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
														.setParameter("login_solicitante", login_solicitante)
														.getResultList();

		if (resultadosSolicitante.isEmpty()) {
			throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
		}

		Usuario solicitante = resultadosSolicitante.get(0);

		List<Usuario> resultadosSuperior = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_superior", Usuario.class)
													.setParameter("login_superior", login_superior)
													.getResultList();

		if (resultadosSuperior.isEmpty()) {
			throw new DataRetrievalFailureException("Usuário superior '" + login_superior + "' não encontrado");
		}

		Usuario superior = resultadosSuperior.get(0);

		List<Usuario> resultadosExistente = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario", Usuario.class)
													.setParameter("login_usuario", novo.getLogin())
													.getResultList();

		if (!resultadosExistente.isEmpty()) {
			throw new DataIntegrityViolationException("Usuário '" + novo.getLogin() + "' já existente");
		}

		Registro registro = new Registro(solicitante, Tipo.INSERIDO, Date.from(LocalDateTime.now()
																							.atZone(ZoneId.systemDefault())
																							.toInstant()));
		novo.setRegistros(new ArrayList<Registro>(Arrays.asList(registro)));

		novo.setSuperior(superior);
		novo.setStatus(Status.ATIVO);

		manager.persist(novo);
	}

	@Transactional
	public void excluir(String login_solicitante,
						String login_usuario)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		List<Usuario> resultadosSolicitante = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
														.setParameter("login_solicitante", login_solicitante)
														.getResultList();

		if (resultadosSolicitante.isEmpty()) {
			throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
		}

		Usuario solicitante = resultadosSolicitante.get(0);

		List<Usuario> resultadosUsuario = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario", Usuario.class)
													.setParameter("login_usuario", login_usuario)
													.getResultList();

		if (resultadosUsuario.isEmpty()) {
			throw new DataRetrievalFailureException("Usuário '" + login_usuario + "' não encontrado");
		}

		Usuario usuario = resultadosUsuario.get(0);

		Registro registro = new Registro(solicitante, Tipo.EXCLUIDO, Date.from(LocalDateTime.now()
																							.atZone(ZoneId.systemDefault())
																							.toInstant()));
		usuario	.getRegistros()
				.add(registro);

		manager.persist(usuario);
	}

	@Transactional
	public void modificar(	String login_solicitante,
							String login_usuario,
							Usuario valores)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		List<Usuario> resultadosSolicitante = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
														.setParameter("login_solicitante", login_solicitante)
														.getResultList();

		if (resultadosSolicitante.isEmpty()) {
			throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
		}

		Usuario solicitante = resultadosSolicitante.get(0);

		List<Usuario> resultadosUsuario = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario", Usuario.class)
													.setParameter("login_usuario", login_usuario)
													.getResultList();

		if (resultadosUsuario.isEmpty()) {
			throw new DataRetrievalFailureException("Usuário '" + login_usuario + "' não encontrado");
		}

		Usuario usuario = resultadosUsuario.get(0);

		if (!valores.getLogin()
					.equals(usuario.getLogin())) {

			List<Usuario> resultadosExistente = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario", Usuario.class)
														.setParameter("login_usuario", valores.getLogin())
														.getResultList();

			if (!resultadosExistente.isEmpty()) {
				throw new DataIntegrityViolationException("Usuário '" + valores.getLogin() + "' já existente");
			}

		}

		Modificador.modificar(usuario, valores);
		Validador.validar(usuario);

		Registro registro = new Registro(solicitante, Tipo.MODIFICADO, Date.from(LocalDateTime	.now()
																								.atZone(ZoneId.systemDefault())
																								.toInstant()));
		usuario	.getRegistros()
				.add(registro);

		manager.persist(usuario);
	}

	// @Transactional
	public Usuario logar(Usuario usuario) {
		List<Usuario> results = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario AND u.senha = :senha_usuario", Usuario.class)
										.setParameter("login_usuario", usuario.getLogin())
										.setParameter("senha_usuario", usuario.getSenha())
										.getResultList();

		return !results.isEmpty() ? null : results.get(0);
	}
}
