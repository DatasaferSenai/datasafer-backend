package datasafer.backup.dao;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@Repository
public class UsuarioDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Usuario obter(String login_usuario) {
		try {
			return manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario", Usuario.class)
							.setParameter("login_usuario", login_usuario)
							.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public void inserir(String login_solicitante, String login_superior, Usuario usuario) {

		usuario.setInseridoEm(Calendar	.getInstance(TimeZone.getDefault())
										.getTime());
		usuario.setInseridoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

		usuario.setSuperior(login_superior == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_superior", Usuario.class)
																	.setParameter("login_superior", login_superior)
																	.getSingleResult());

		manager.persist(usuario);
	}

	@Transactional
	public void modificar(String login_solicitante, Usuario usuario) {

		usuario = manager.merge(usuario);

		usuario.setModificadoEm(Calendar.getInstance(TimeZone.getDefault())
										.getTime());
		usuario.setModificadoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

	}

	// @Transactional
	public List<Usuario> listarUsuarios(String login_superior) {
		try {
			return manager	.createQuery("SELECT u FROM Usuario u WHERE u.superior.login = :login_superior ", Usuario.class)
							.setParameter("login_superior", login_superior)
							.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	// @Transactional
	public List<Privilegio> listarPrivilegios(String login_proprietario) {
		try {
			return manager	.createQuery("SELECT p FROM Privilegio p WHERE p.proprietario.login = :login_proprietario", Privilegio.class)
							.setParameter("login_proprietario", login_proprietario)
							.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	// @Transactional
	public List<Estacao> listarEstacoes(String login_proprietario) {
		try {
			return manager	.createQuery("SELECT e FROM Estacao e WHERE e.proprietario.login = :login_proprietario", Estacao.class)
							.setParameter("login_proprietario", login_proprietario)
							.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	// @Transactional
	public List<Backup> listarBackups(String login_proprietario) {
		try {
			return manager	.createQuery("SELECT b FROM Backup b WHERE b.proprietario.login = :login_proprietario", Backup.class)
							.setParameter("login_proprietario", login_proprietario)
							.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	// @Transactional
	public List<Operacao> listarOperacoes(String login_proprietario) {
		try {
			return manager	.createQuery("SELECT o FROM Operacao o WHERE o.proprietario.login = :login_proprietario", Operacao.class)
							.setParameter("login_proprietario", login_proprietario)
							.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	// @Transactional
	public Usuario logar(Usuario usuario) {

		try {
			return manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario AND u.senha = :senha_usuario", Usuario.class)
							.setParameter("login_usuario", usuario.getLogin())
							.setParameter("senha_usuario", usuario.getSenha())
							.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
