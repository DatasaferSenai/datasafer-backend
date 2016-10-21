package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Host;
import datasafer.backup.model.Usuario;

@Repository
public class HostDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Host obter(String login_usuario, String nome_host) {
		TypedQuery<Host> query = manager.createQuery(
				"SELECT h FROM Host h WHERE h.proprietario.login = :login_usuario AND h.nome = :nome_host", Host.class);
		query.setParameter("login_usuario", login_usuario);
		query.setParameter("nome_host", nome_host);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional
	public void modificar(Host host) {
		manager.merge(host);
	}

	@Transactional
	public void inserir(String login_usuario, Host host) {
		TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario",
				Usuario.class);
		query.setParameter("login_usuario", login_usuario);
		host.setProprietario(query.getSingleResult());
		manager.persist(host);
	}

}
