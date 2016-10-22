package datasafer.backup.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
	public void inserir(String login_solicitante, String login_proprietario, String nome_estacao, String nome_backup,
			Operacao operacao) {

		operacao.setInseridoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());

		if (login_solicitante != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);

			operacao.setInseridoPor(query.getSingleResult());

		} else {
			operacao.setInseridoPor(null);
		}

		if (login_proprietario != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario", Usuario.class);
			query.setParameter("login_proprietario", login_proprietario);

			operacao.setProprietario(query.getSingleResult());
		} else {
			operacao.setProprietario(null);
		}
		
		TypedQuery<Backup> query = manager.createQuery(
				"SELECT b FROM Backup b WHERE b.estacao.proprietario.login = :login_proprietario AND b.estacao.nome = :nome_estacao AND b.nome = :nome_backup",
				Backup.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_estacao", nome_estacao);
		query.setParameter("nome_backup", nome_backup);
		operacao.setBackup(query.getSingleResult());

		manager.persist(operacao);
	}

	@Transactional
	public void modificar(String login_solicitante, Operacao operacao) {

		operacao.setModificadoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());

		if (login_solicitante != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);

			operacao.setModificadoPor(query.getSingleResult());

		} else {
			operacao.setModificadoPor(null);
		}

		manager.merge(operacao);
	}

	// @Transactional
	public Operacao obter(String login_proprietario, String nome_estacao, String nome_backup, Date data_operacao) {
		TypedQuery<Operacao> query = manager.createQuery(
				"SELECT o FROM Operacao o WHERE o.backup.estacao.proprietario.login = :login_proprietario AND o.backup.estacao.nome = :nome_estacao AND o.backup.nome = :nome_backup AND o.data = :data_operacao",
				Operacao.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_estacao", nome_estacao);
		query.setParameter("nome_backup", nome_backup);
		query.setParameter("data_operacao", data_operacao);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}
