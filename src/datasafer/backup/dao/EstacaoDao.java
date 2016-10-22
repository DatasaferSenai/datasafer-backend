package datasafer.backup.dao;

import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Estacao;
import datasafer.backup.model.Usuario;

@Repository
public class EstacaoDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Estacao obter(String login_proprietario, String nome_estacao) {
		TypedQuery<Estacao> query = manager.createQuery(
				"SELECT e FROM Estacao e WHERE e.proprietario.login = :login_proprietario AND e.nome = :nome_estacao", Estacao.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_estacao", nome_estacao);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional
	public void modificar(String login_solicitante, Estacao estacao) {
		
		estacao.setModificadoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
		
		if (login_solicitante != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);

			estacao.setModificadoPor(query.getSingleResult());

		} else {
			estacao.setModificadoPor(null);
		}
		
		manager.merge(estacao);
	}

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, Estacao estacao) {
		
		estacao.setInseridoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
		
		if(login_solicitante != null){
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante",
					Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);
			
			estacao.setInseridoPor(query.getSingleResult());
			
		} else {
			estacao.setInseridoPor(null);
		}
		
		if(login_proprietario != null){
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario",
					Usuario.class);
			query.setParameter("login_proprietario", login_proprietario);
			
			estacao.setProprietario(query.getSingleResult());
		}
		else {
			estacao.setProprietario(null);
		}
		
		manager.persist(estacao);
	}
	
	@Transactional
	public void excluir(String login_proprietario, String nome_estacao) {
		
		TypedQuery<Estacao> query = manager.createQuery(
				"SELECT e FROM Estacao e WHERE e.proprietario.login = :login_proprietario AND e.nome = :nome_estacao", Estacao.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_estacao", nome_estacao);
		
		manager.remove(query.getSingleResult());
	}

}
