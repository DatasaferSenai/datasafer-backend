package datasafer.backup.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
						Usuario usuario)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		Validador.validar(usuario);

		Usuario solicitante = this.obter(login_solicitante);
		if (solicitante == null) {
			throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
		}

		Usuario superior = this.obter(login_superior);
		if (superior == null) {
			throw new DataRetrievalFailureException("Usuário superior '" + login_superior + "' não encontrado");
		}

		Usuario existente = this.obter(usuario.getLogin());
		if (existente != null && existente	.getUltimoRegistro()
											.getTipo() != Tipo.EXCLUIDO) {
			throw new DataIntegrityViolationException("Usuário '" + usuario.getLogin() + "' já existente");
		}

		List<Registro> registros = usuario.getRegistros();
		if (registros == null) {
			registros = new ArrayList<Registro>();
			usuario.setRegistros(registros);
		}
		registros.add(new Registro(solicitante, Tipo.INSERIDO, Date.from(LocalDateTime	.now()
																						.atZone(ZoneId.systemDefault())
																						.toInstant())));

		usuario.setSuperior(superior);
		usuario.setStatus(Status.ATIVO);

		manager.persist(usuario);
	}

	@Transactional
	public void excluir(String login_solicitante,
						String login_usuario)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		Usuario solicitante = this.obter(login_solicitante);
		if (solicitante == null) {
			throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
		}

		Usuario usuario = this.obter(login_usuario);
		if (usuario == null) {
			throw new DataRetrievalFailureException("Usuário superior '" + login_usuario + "' não encontrado");
		}

		List<Registro> registros = usuario.getRegistros();
		if (registros == null) {
			registros = new ArrayList<Registro>();
			usuario.setRegistros(registros);
		}
		registros.add(new Registro(solicitante, Tipo.EXCLUIDO, Date.from(LocalDateTime	.now()
																						.atZone(ZoneId.systemDefault())
																						.toInstant())));

		manager.persist(usuario);
	}

	@Transactional
	public void modificar(	String login_solicitante,
							String login_usuario,
							Usuario valores)
			throws DataRetrievalFailureException, DataIntegrityViolationException {

		Usuario solicitante = this.obter(login_solicitante);
		if (solicitante == null) {
			throw new DataRetrievalFailureException("Usuário solicitante '" + login_solicitante + "' não encontrado");
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

		Modificador.modificar(usuario, valores);
		Validador.validar(usuario);

		List<Registro> registros = usuario.getRegistros();
		if (registros == null) {
			registros = new ArrayList<Registro>();
			usuario.setRegistros(registros);
		}
		registros.add(new Registro(solicitante, Tipo.MODIFICADO, Date.from(LocalDateTime.now()
																						.atZone(ZoneId.systemDefault())
																						.toInstant())));

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
