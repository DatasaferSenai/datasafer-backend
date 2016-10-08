package datasafer.backup.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Host;
import datasafer.backup.model.Usuario;

@Repository
public class UsuarioDao {

	@PersistenceContext
	private EntityManager manager;

	@Transactional
	public void inserir(Usuario usuario) {
		manager.persist(usuario);
	}

	// @Transactional
	public Usuario obter(Long idUsuario) {
		return manager.find(Usuario.class, idUsuario);
	}

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

	// @Transactional
	public List<Host> listarHosts(String login_usuario) {
		TypedQuery<Host> query = manager.createQuery("SELECT h FROM Host h WHERE h.usuario.login = :login_usuario",
				Host.class);
		query.setParameter("login_usuario", login_usuario);
		return query.getResultList();
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
			e.printStackTrace();
			return null;
		}
	}

}
