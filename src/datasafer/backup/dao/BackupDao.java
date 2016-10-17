package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Host;

@Repository
public class BackupDao {

	@PersistenceContext
	private EntityManager manager;
	
	@Transactional
	public Backup inserirBackup(String login_usuario, String nome_host, Backup backup) {
		TypedQuery<Host> query = manager.createQuery(
				"SELECT h FROM Host h WHERE h.usuario.login = :login_usuario AND h.nome = :nome_host", Host.class);
		query.setParameter("login_usuario", login_usuario);
		query.setParameter("nome_host", nome_host);

		Host host = query.getSingleResult();
		backup.setHost(host);
		manager.persist(backup);
		
		return backup;
	}
	
	
	@Transactional
	public void modificarBackup(Backup backup) {
		manager.persist(backup);
	}

	// @Transactional
	public Backup obterBackup(String login_usuario, String nome_host, String nome_backup) {
		TypedQuery<Backup> query = manager.createQuery(
				"SELECT b FROM Backup b WHERE b.host.usuario.login = :login_usuario AND b.host.nome = :nome_host AND b.nome = :nome_backup",
				Backup.class);
		query.setParameter("login_usuario", login_usuario);
		query.setParameter("nome_host", nome_host);
		query.setParameter("nome_backup", nome_backup);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}
