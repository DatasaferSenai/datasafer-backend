package datasafer.backup.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@Repository
public class UsuarioDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Usuario obter(String login_usuario) {
		TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario",
				Usuario.class);
		query.setParameter("login_usuario", login_usuario);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public void inserir(String login_solicitante, String login_superior, Usuario usuario) {
		
		if(login_solicitante != null){
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante",
					Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);
			
			usuario.setInseridoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
			usuario.setInseridoPor(query.getSingleResult());
			
		} else {
			usuario.setInseridoEm(null);
			usuario.setInseridoPor(null);
		}
		
		if(login_superior != null){
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_superior",
					Usuario.class);
			query.setParameter("login_superior", login_superior);
			
			usuario.setSuperior(query.getSingleResult());
		} else {
			usuario.setSuperior(null);
		}
		
		
		manager.persist(usuario);
	}

	@Transactional
	public void modificar(String login_solicitante, Usuario usuario) {
		
		if(login_solicitante != null){
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario",
					Usuario.class);
			query.setParameter("login_usuario", login_solicitante);
			
			usuario.setModificadoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
			usuario.setModificadoPor(query.getSingleResult());
			
		} else {
			usuario.setModificadoEm(null);
			usuario.setModificadoPor(null);
		}
		
		manager.merge(usuario);
	}

	@Transactional
	public void modificarPrivilegio(String login_usuario, Privilegio privilegio) {
		Usuario usuario = this.obter(login_usuario);
		usuario.setPrivilegio(privilegio);
		manager.merge(usuario);
	}

	// @Transactional
	public List<Usuario> listaUsuarios() {
		TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u", Usuario.class);
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	// @Transactional
	public Usuario logar(Usuario usuario) {
		TypedQuery<Usuario> query = manager.createQuery(
				"SELECT u FROM Usuario u WHERE u.login = :login_usuario AND u.senha = :senha_usuario", Usuario.class);
		query.setParameter("login_usuario", usuario.getLogin());
		query.setParameter("senha_usuario", usuario.getSenha());
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}
