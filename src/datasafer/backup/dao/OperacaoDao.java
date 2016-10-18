package datasafer.backup.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Backup;
import datasafer.backup.model.Operacao;

@Repository
public class OperacaoDao {

	@PersistenceContext
	private EntityManager manager;

	@Transactional
	public void modificarOperacao(Operacao operacao) {
		manager.persist(operacao);
	}

	@Transactional(noRollbackFor = Exception.class)
	public Operacao inserirOperacao(String login_usuario, String nome_host, String nome_backup, Operacao operacao) {
		TypedQuery<Backup> query = manager.createQuery(
				"SELECT b FROM Backup b WHERE b.host.usuario.login = :login_usuario AND b.host.nome = :nome_host AND b.nome = :nome_backup",
				Backup.class);
		query.setParameter("login_usuario", login_usuario);
		query.setParameter("nome_host", nome_host);
		query.setParameter("nome_backup", nome_backup);

		try {
			operacao.setBackup(query.getSingleResult());
			manager.persist(operacao);
			return operacao;
		} catch (Exception e) {
			return null;
		}
	}

	// @Transactional
	public Operacao obterOperacao(String login_usuario, String nome_host, String nome_backup, Date data_operacao) {
		TypedQuery<Operacao> query = manager.createQuery(
				"SELECT o FROM Operacao o WHERE o.backup.host.usuario.login = :login_usuario AND o.backup.host.nome = :nome_host AND o.backup.nome = :nome_backup AND o.data = :data_operacao",
				Operacao.class);
		query.setParameter("login_usuario", login_usuario);
		query.setParameter("nome_host", nome_host);
		query.setParameter("nome_backup", nome_backup);
		query.setParameter("data_operacao", data_operacao);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}
