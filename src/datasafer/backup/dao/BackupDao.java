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
import datasafer.backup.model.Usuario;

@Repository
public class BackupDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Backup obter(String login_proprietario, String nome_estacao, String nome_backup) {
		List<Backup> results = manager	.createQuery(
				"SELECT b FROM Backup b WHERE b.estacao.nome = :nome_estacao AND b.proprietario.login = :login_proprietario AND b.nome = :nome_backup",
				Backup.class)
										.setParameter("login_proprietario", login_proprietario)
										.setParameter("nome_estacao", nome_estacao)
										.setParameter("nome_backup", nome_backup)
										.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, String nome_estacao, Backup backup) {

		backup.setInseridoEm(Calendar	.getInstance(TimeZone.getDefault())
										.getTime());
		backup.setInseridoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

		backup.setProprietario(
				login_proprietario == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario", Usuario.class)
															.setParameter("login_proprietario", login_proprietario)
															.getSingleResult());

		backup.setEstacao(
				manager	.createQuery("SELECT e FROM Estacao e WHERE e.nome = :nome_estacao", Estacao.class)
						.setParameter("nome_estacao", nome_estacao)
						.getSingleResult());

		manager.persist(backup);
	}

	@Transactional
	public void modificar(String login_solicitante, Backup backup) {

		backup = manager.merge(backup);

		backup.setModificadoEm(Calendar	.getInstance(TimeZone.getDefault())
										.getTime());
		backup.setModificadoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

	}

	// @Transactional
	public List<Operacao> listarOperacoes(String login_proprietario, String nome_estacao, String nome_backup) {
		return manager	.createQuery(
				"SELECT o FROM Operacao o WHERE o.proprietario.login = :login_proprietario AND o.backup.estacao.nome = :nome_estacao AND o.backup.nome = :nome_backup ",
				Operacao.class)
						.setParameter("login_proprietario", login_proprietario)
						.setParameter("nome_estacao", nome_estacao)
						.setParameter("nome_backup", nome_backup)
						.getResultList();
	}

}
