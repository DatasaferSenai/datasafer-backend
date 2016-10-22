package datasafer.backup.dao;

import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Privilegio;
import datasafer.backup.model.Usuario;

@Repository
public class PrivilegioDao {

	@PersistenceContext
	private EntityManager manager;

	@Transactional
	public void inserir(String login_solicitante, String login_proprietario, Privilegio privilegio) {

		privilegio.setInseridoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
		
		if (login_solicitante != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante", Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);

			privilegio.setInseridoPor(query.getSingleResult());
		} else {
			privilegio.setInseridoPor(null);
		}

		if (login_proprietario != null) {
			TypedQuery<Usuario> query = manager
					.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_proprietario", Usuario.class);
			query.setParameter("login_proprietario", login_proprietario);

			privilegio.setProprietario(query.getSingleResult());
		} else {
			privilegio.setProprietario(null);
		}

		manager.persist(privilegio);
	}

	@Transactional
	public void modificar(String login_solicitante, Privilegio privilegio) {
		
		privilegio.setModificadoEm(Calendar.getInstance(TimeZone.getDefault()).getTime());
		
		if(login_solicitante != null){
			TypedQuery<Usuario> query = manager.createQuery("SELECT u FROM Usuario u WHERE u.login = :login_solicitante",
					Usuario.class);
			query.setParameter("login_solicitante", login_solicitante);
			
			privilegio.setModificadoPor(query.getSingleResult());
			
		} else {
			privilegio.setModificadoPor(null);
		}
		
		manager.merge(privilegio);
	}

	// @Transactional
	public Privilegio obter(String nome_privilegio) {
		TypedQuery<Privilegio> query = manager.createQuery("SELECT p FROM Privilegio p WHERE p.nome = :nome_privilegio",
				Privilegio.class);
		query.setParameter("nome_privilegio", nome_privilegio);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}
