package datasafer.backup.dao;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
		try {
			return manager	.createQuery("SELECT e FROM Estacao e WHERE e.proprietario.login = :login_proprietario AND e.nome = :nome_estacao", Estacao.class)
							.setParameter("login_proprietario", login_proprietario)
							.setParameter("nome_estacao", nome_estacao)
							.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Transactional
	public void modificar(String login_solicitante, Estacao estacao) {

		estacao.setModificadoEm(Calendar.getInstance(TimeZone.getDefault())
										.getTime());
		estacao.setModificadoPor(login_solicitante == null ? null
				: manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante",
						Usuario.class)
							.setParameter("login_solicitante", login_solicitante)
							.getSingleResult());

		manager.merge(estacao);
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

}
