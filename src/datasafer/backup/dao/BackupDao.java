package datasafer.backup.dao;

import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Host;
import datasafer.backup.model.Usuario;

@Repository
public class BackupDao {

	@PersistenceContext
	private EntityManager manager;

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, String nome_host, Backup backup) {
		TypedQuery<Host> query = manager.createQuery(
				"SELECT h FROM Host h WHERE h.proprietario.login = :login_proprietario AND h.nome = :nome_host", Host.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_host", nome_host);
		backup.setHost(query.getSingleResult());
		
		
		manager.persist(backup);
	}

	// @Transactional
	public Backup obter(String login_proprietario, String nome_host, String nome_backup) {
		TypedQuery<Backup> query = manager.createQuery(
				"SELECT b FROM Backup b WHERE b.host.proprietario.login = :login_proprietario AND b.host.nome = :nome_host AND b.nome = :nome_backup",
				Backup.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_host", nome_host);
		query.setParameter("nome_backup", nome_backup);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional
	public void modificar(String login_solicitante, Backup backup) {
		
		if (login_solicitante != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);

			backup.setModificadoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
			backup.setModificadoPor(query.getSingleResult());

		} else {
			backup.setModificadoEm(null);
			backup.setModificadoPor(null);
		}
		
		manager.merge(backup);
	}



}
