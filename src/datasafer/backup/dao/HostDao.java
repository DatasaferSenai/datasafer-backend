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
public class HostDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Host obter(String login_proprietario, String nome_host) {
		TypedQuery<Host> query = manager.createQuery(
				"SELECT h FROM Host h WHERE h.proprietario.login = :login_proprietario AND h.nome = :nome_host", Host.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_host", nome_host);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional
	public void modificar(String login_solicitante, Host host) {
		
		if (login_solicitante != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);

			host.setModificadoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
			host.setModificadoPor(query.getSingleResult());

		} else {
			host.setModificadoEm(null);
			host.setModificadoPor(null);
		}
		
		manager.merge(host);
	}

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, Host host) {
		
		if(login_solicitante != null){
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante",
					Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);
			
			host.setInseridoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
			host.setInseridoPor(query.getSingleResult());
			
		} else {
			host.setInseridoEm(null);
			host.setInseridoPor(null);
		}
		
		if(login_proprietario != null){
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario",
					Usuario.class);
			query.setParameter("login_proprietario", login_proprietario);
			
			host.setProprietario(query.getSingleResult());
		}
		else {
			host.setProprietario(null);
		}
		
		manager.persist(host);
	}
	
	@Transactional
	public void excluir(String login_proprietario, String nome_host) {
		
		TypedQuery<Host> query = manager.createQuery(
				"SELECT h FROM Host h WHERE h.proprietario.login = :login_proprietario AND h.nome = :nome_host", Host.class);
		query.setParameter("login_proprietario", login_proprietario);
		query.setParameter("nome_host", nome_host);
		
		manager.remove(query.getSingleResult());
	}

}
