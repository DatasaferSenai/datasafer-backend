package datasafer.backup.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import datasafer.backup.modelo.Usuario;

@Repository
public class DatasaferDao {

	@PersistenceContext
	private EntityManager manager;

	public void inserir(Usuario usuario) {
		manager.persist(usuario);
	}
}
