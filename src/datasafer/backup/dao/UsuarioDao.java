package datasafer.backup.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Registro;
import datasafer.backup.model.Registro.Tipo;
import datasafer.backup.model.Usuario;

@Repository
public class UsuarioDao {

	@PersistenceContext
	private EntityManager manager;

	// @Transactional
	public Usuario obter(String login_usuario) {
		List<Usuario> results = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario", Usuario.class)
										.setParameter("login_usuario", login_usuario)
										.getResultList();

		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public void inserir(String login_solicitante, String login_superior, Usuario usuario) {

		Registro registro = new Registro();
		registro.setSolicitante(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());
		registro.setData(Calendar	.getInstance(TimeZone.getDefault())
									.getTime());
		registro.setTipo(Tipo.INSERIDO);

		List<Registro> registros = usuario.getRegistros();
		if (registros == null) {
			registros = new ArrayList<Registro>();
		}
		registros.add(registro);
		usuario.setRegistros(registros);

		usuario.setSuperior(login_superior == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_superior", Usuario.class)
																	.setParameter("login_superior", login_superior)
																	.getSingleResult());

		manager.persist(usuario);
	}

	@Transactional
	public void modificar(String login_solicitante, Usuario usuario) {

		usuario = manager.merge(usuario);

		Registro registro = new Registro();
		registro.setSolicitante(
				login_solicitante == null ? null : manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class)
															.setParameter("login_solicitante", login_solicitante)
															.getSingleResult());
		registro.setData(Calendar	.getInstance(TimeZone.getDefault())
									.getTime());
		registro.setTipo(Tipo.MODIFICADO);

		usuario	.getRegistros()
				.add(registro);
	}

	// @Transactional
	public Usuario logar(Usuario usuario) {
		List<Usuario> results = manager	.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_usuario AND u.senha = :senha_usuario", Usuario.class)
										.setParameter("login_usuario", usuario.getLogin())
										.setParameter("senha_usuario", usuario.getSenha())
										.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}
}
