package datasafer.backup.dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.model.Permissao;
import datasafer.backup.model.Usuario;

@Repository
public class PermissaoDao {

	@PersistenceContext
	private EntityManager manager;

	public <T> List<Permissao> obtemPermissoes(T objeto) {
		try {
			return manager	.createQuery("SELECT p FROM " + objeto.getClass().getSimpleName() + " o "
					+ "INNER JOIN o.permissoes p "
					+ "WHERE o.id = :id_objeto ", Permissao.class)
							.setParameter("id_objeto", (Long) new PropertyDescriptor("id", objeto.getClass())	.getReadMethod()
																												.invoke(objeto))
							.getResultList();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			e.printStackTrace();
		}
		return new ArrayList<Permissao>();
	}

	// @Transactional
	public List<Permissao> obtemPermissoesRecebidas(Usuario recebedor) {

		return manager	.createQuery(
									"SELECT p FROM Permissao p "
											+ "WHERE p.recebedor.id = :id_recebedor ",
									Permissao.class)
						.setParameter("id_recebedor", recebedor.getId())
						.getResultList();
	}

	// @Transactional
	public List<Permissao> obtemPermissoesAtribuidas(Usuario atribuidor) {

		return manager	.createQuery(
									"SELECT p FROM Permissao p "
											+ "WHERE p.atribuidor.id = :id_atribuidor ",
									Permissao.class)
						.setParameter("id_atribuidor", atribuidor.getId())
						.getResultList();
	}

	public <T> Permissao obtemPermissao(Usuario recebedor,
										T objeto,
										String atributo) {

		try {

			List<Permissao> resultadosPermissao = manager	.createQuery(
																		"SELECT p FROM " + objeto.getClass().getSimpleName() + " o "
																				+ "INNER JOIN o.permissoes p "
																				+ "WHERE o.id = :id_objeto "
																				+ "AND p.recebedor.id = :id_recebedor "
																				+ "AND p.atributo = :atributo ",
																		Permissao.class)
															.setParameter("id_recebedor", recebedor.getId())
															.setParameter("atributo", atributo)
															.setParameter("id_objeto", (Long) new PropertyDescriptor("id", objeto.getClass())	.getReadMethod()
																																				.invoke(objeto))
															.getResultList();

			return resultadosPermissao.isEmpty()	? null
													: resultadosPermissao.get(0);

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public <T> void inserirPermissao(	T objeto,
										Permissao permissao) {

		try {
			Usuario atribuidor = (permissao.getAtribuidor() == null	? null
																	: manager.find(Usuario.class, permissao.getAtribuidor().getId()));

			Usuario recebedor = manager.find(Usuario.class, permissao.getRecebedor().getId());

			objeto = (T) manager.find(objeto.getClass(), (Long) new PropertyDescriptor("id", objeto.getClass()).getReadMethod().invoke(objeto));

			if (this.obtemPermissao(recebedor, objeto, permissao.getAtributo()) != null) {
				throw new DataIntegrityViolationException("Permissão do atributo " + permissao.getAtributo() + " existe na entidade "
						+ objeto.getClass().getName());
			}

			if (atribuidor != null) {
				this.obtemPermissoesAtribuidas(atribuidor).add(permissao);
				permissao.setAtribuidor(atribuidor);
			}

			this.obtemPermissoesRecebidas(recebedor).add(permissao);
			permissao.setRecebedor(recebedor);

			((List<Permissao>) new PropertyDescriptor("permissoes", objeto.getClass()).getReadMethod().invoke(objeto)).add(permissao);
			
			manager.persist(permissao);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
