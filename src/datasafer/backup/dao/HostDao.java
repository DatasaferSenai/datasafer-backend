package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import datasafer.backup.model.Host;

@Repository
public class HostDao {

	@PersistenceContext
	private EntityManager manager;
	
	//@Transactional
	public Host inserir(Long id_host){
		return manager.find(Host.class, id_host);
	}
	
	//@Transactional
	public Host obter(Long id_host){
		return manager.find(Host.class, id_host);
	}
	
	//@Transactional
	public Host obter(String login_usuario, String nome_host){
		TypedQuery<Host> query = manager.createQuery("SELECT h FROM Host h WHERE h.usuario.login = :login_usuario AND h.nome = :nome_host",Host.class); 
		query.setParameter("login_usuario", login_usuario);
		query.setParameter("nome_host", nome_host);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}	
	
	
}
