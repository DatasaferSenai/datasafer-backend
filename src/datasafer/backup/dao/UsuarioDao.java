package datasafer.backup.dao;

import java.util.List;

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

	@Transactional
	public void inserirUsuario(String login_usuario, Usuario usuario) {
		manager.persist(usuario);
	}

	@Transactional
	public void modificarUsuario(Usuario usuario) {
		manager.merge(usuario);
	}

	@Transactional
	public void modificarPrivilegio(String login_usuario, Privilegio privilegio) {
		Usuario usuario = this.obterUsuario(login_usuario);
		usuario.setPrivilegio(privilegio);
		manager.merge(usuario);
	}

	// @Transactional
	public Usuario obterUsuario(String login_usuario) {
		TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario",
				Usuario.class);
		query.setParameter("login_usuario", login_usuario);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
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
