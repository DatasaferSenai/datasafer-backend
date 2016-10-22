package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@Repository
public class PrivilegioDao {

	@PersistenceContext
	private EntityManager manager;

	@Transactional
	public void inserirPrivilegio(String login_usuario, Privilegio privilegio) {
		if (login_usuario != null) {
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario",
					Usuario.class);
			query.setParameter("login_usuario", login_usuario);
			privilegio.setInseridoPor(query.getSingleResult());
		} else {
			privilegio.setInseridoPor(null);
		}
		manager.persist(privilegio);
	}

	@Transactional
	public void modificarPrivilegio(Privilegio privilegio) {
		manager.merge(privilegio);
	}

	// @Transactional
	public Privilegio obterPrivilegio(String nome_privilegio) {
		TypedQuery<Privilegio> query = manager.createQuery("SELECT p FROM Privilegio p WHERE p.nome = :nome_privilegio",
				Privilegio.class);
		query.setParameter("nome_privilegio", nome_privilegio);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}
