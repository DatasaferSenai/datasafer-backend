package datasafer.backup.dao;

import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@Repository
public class BackupDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Backup obter(String login_proprietario, String nome_estacao, String nome_backup) {
		TypedQuery<Backup> query = manager.createQuery(
				"SELECT b FROM Backup b WHERE b.estacao.proprietario.login = :login_proprietario AND b.estacao.nome = :nome_estacao AND b.nome = :nome_backup",
				Backup.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_estacao", nome_estacao);
		query.setParameter("nome_backup", nome_backup);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, String nome_estacao, Backup backup) {

		backup.setInseridoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());

		if (login_solicitante != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);

			backup.setInseridoPor(query.getSingleResult());

		} else {
			backup.setInseridoPor(null);
		}

		TypedQuery<Estacao> query = manager.createQuery(
				"SELECT e FROM Estacao e WHERE e.proprietario.login = :login_proprietario AND e.nome = :nome_estacao",
				Estacao.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_estacao", nome_estacao);
		backup.setEstacao(query.getSingleResult());
		
		manager.persist(backup);
	}

	@Transactional
	public void modificar(String login_solicitante, Backup backup) {

		backup.setModificadoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());

		if (login_solicitante != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);

			backup.setModificadoPor(query.getSingleResult());

		} else {
			backup.setModificadoPor(null);
		}

		manager.merge(backup);
	}

}
