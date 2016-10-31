package datasafer.backup.dao;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@Repository
public class PrivilegioDao {

	@PersistenceContext
	private EntityManager manager;

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, Privilegio privilegio) {

		privilegio.setInseridoEm(Calendar	.getInstance(TimeZone.getDefault())
											.getTime());
		privilegio.setInseridoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

		privilegio.setProprietario(
				login_proprietario == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario", Usuario.class)
															.setParameter("login_proprietario", login_proprietario)
															.getSingleResult());

		manager.persist(privilegio);
	}

	@Transactional
	public void modificar(String login_solicitante, Privilegio privilegio) {

		privilegio = manager.merge(privilegio);

		privilegio.setModificadoEm(Calendar	.getInstance(TimeZone.getDefault())
											.getTime());
		privilegio.setModificadoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());
	}

	// @Transactional
	public Privilegio obter(String login_proprietario, String nome_privilegio) {
		List<Privilegio> results = manager	.createQuery(
				"SELECT p FROM Privilegio p WHERE p.nome = :nome_privilegio AND p.proprietario.login = :login_proprietario", Privilegio.class)
											.setParameter("nome_privilegio", nome_privilegio)
											.setParameter("login_proprietario", login_proprietario)
											.getResultList();

		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public void atribuir(String login_solicitante, String login_proprietario, String nome_privilegio, String login_usuario) {

		Usuario usuario = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario", Usuario.class)
									.setParameter("login_usuario", login_usuario)
									.getSingleResult();

		usuario.setModificadoEm(Calendar.getInstance(TimeZone.getDefault())
										.getTime());
		usuario.setModificadoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

		usuario.setPrivilegio(manager	.createQuery(
				"SELECT p FROM Privilegio p WHERE p.nome = :nome_privilegio AND p.proprietario.login = :login_proprietario",
				Privilegio.class)
										.setParameter("nome_privilegio", nome_privilegio)
										.setParameter("login_proprietario", login_proprietario)
										.getSingleResult());

		//manager.merge(usuario);
	}

}
