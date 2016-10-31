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
public class EstacaoDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Estacao obter(String login_proprietario, String nome_estacao) {
		List<Estacao> results = manager	.createQuery("SELECT e FROM Estacao e WHERE e.proprietario.login = :login_proprietario AND e.nome = :nome_estacao",
				Estacao.class)
										.setParameter("login_proprietario", login_proprietario)
										.setParameter("nome_estacao", nome_estacao)
										.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public void modificar(String login_solicitante, Estacao estacao) {

		estacao = manager.merge(estacao);

		estacao.setModificadoEm(Calendar.getInstance(TimeZone.getDefault())
										.getTime());
		estacao.setModificadoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());
	}

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, Estacao estacao) {

		estacao.setInseridoEm(Calendar	.getInstance(TimeZone.getDefault())
										.getTime());
		estacao.setInseridoPor(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());

		estacao.setProprietario(
				login_proprietario == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario", Usuario.class)
															.setParameter("login_proprietario", login_proprietario)
															.getSingleResult());

		manager.persist(estacao);
	}

	// @Transactional
	public List<Backup> listarBackups(String login_proprietario, String nome_estacao) {
		return manager	.createQuery("SELECT b FROM Backup b WHERE b.proprietario.login = :login_proprietario AND b.estacao.nome = :nome_estacao", Backup.class)
						.setParameter("login_proprietario", login_proprietario)
						.setParameter("nome_estacao", nome_estacao)
						.getResultList();
	}

	// @Transactional
	public List<Operacao> listarOperacoes(String login_proprietario, String nome_estacao) {
		return manager	.createQuery("SELECT o FROM Operacao o WHERE o.proprietario.login = :login_proprietario AND o.backup.estacao.nome = :nome_estacao",
				Operacao.class)
						.setParameter("login_proprietario", login_proprietario)
						.setParameter("nome_estacao", nome_estacao)
						.getResultList();
	}

}
