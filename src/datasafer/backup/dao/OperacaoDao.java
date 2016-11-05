package datasafer.backup.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;
import datasafer.backup.model.Usuario;

@Repository
public class OperacaoDao {

	@PersistenceContext
	private EntityManager manager;

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, String nome_estacao, String nome_backup, Operacao operacao) {

		operacao.setInseridoEm(Calendar	.getInstance(TimeZone.getDefault())
										.getTime());
		operacao.setInseridoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

		operacao.setBackup(manager	.createQuery(
				"SELECT b FROM Backup b WHERE b.proprietario.login = :login_proprietario AND b.estacao.nome = :nome_estacao AND b.nome = :nome_backup",
				Backup.class)
									.setParameter("login_proprietario", login_proprietario)
									.setParameter("nome_estacao", nome_estacao)
									.setParameter("nome_backup", nome_backup)
									.getSingleResult());

		manager.persist(operacao);
	}

	@Transactional
	public void modificar(String login_solicitante, Operacao operacao) {

		operacao = manager.merge(operacao);

		operacao.setModificadoEm(Calendar	.getInstance(TimeZone.getDefault())
											.getTime());
		operacao.setModificadoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());
	}

	// @Transactional
	public Operacao obter(String login_proprietario, String nome_estacao, String nome_backup, Date data_operacao) {
		List<Operacao> results = manager.createQuery(
				"SELECT o FROM Operacao o WHERE o.backup.proprietario.login = :login_proprietario AND o.backup.estacao.nome = :nome_estacao AND o.backup.nome = :nome_backup AND o.data = :data_operacao",
				Operacao.class)
										.setParameter("login_proprietario", login_proprietario)
										.setParameter("nome_estacao", nome_estacao)
										.setParameter("nome_backup", nome_backup)
										.setParameter("data_operacao", data_operacao)
										.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

}
