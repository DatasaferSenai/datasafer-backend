package datasafer.backup.dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.utility.Carregador;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Permissao.Tipo;
import datasafer.backup.model.Usuario;

@Repository
public class PermissaoDao {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private Carregador carregador;

	public <T> List<Permissao> obtemPermissoes(T objeto) {
		try {
			return manager	.createQuery(
										"SELECT p FROM " + objeto.getClass().getSimpleName() + " o "
												+ "INNER JOIN o.permissoes p "
												+ "WHERE o.id = :id_objeto ",
										Permissao.class)
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

	public <T> boolean temPermissao(Usuario recebedor,
									T objeto,
									String atributo,
									Tipo tipo) throws NoSuchFieldException {

		{
			Permissao permissao = this.obtemPermissao(recebedor, objeto, atributo, tipo);
			if (permissao != null) {
				return permissao.isPermitido();
			}
		}

		{

			Usuario usuarioAux = (Usuario) carregador.obtemAtributo(objeto, "proprietario");
			if (usuarioAux != null) {
				while (usuarioAux != null) {
					Permissao permissao = this.obtemPermissao(recebedor, usuarioAux, atributo, tipo);
					if (permissao != null) {
						return permissao.isPermitido();
					}
					usuarioAux = (Usuario) carregador.obtemAtributo(usuarioAux, "proprietario");
				}
			}
		}

		{
			Permissao permissao = this.obtemPermissao(recebedor, objeto, null, tipo);
			if (permissao != null) {
				return permissao.isPermitido();
			}
		}

		{
			Usuario usuarioAux = (Usuario) carregador.obtemAtributo(objeto, "proprietario");
			if (usuarioAux != null) {
				while (usuarioAux != null) {
					Permissao permissao = this.obtemPermissao(recebedor, usuarioAux, null, tipo);
					if (permissao != null) {
						return permissao.isPermitido();
					}
					usuarioAux = (Usuario) carregador.obtemAtributo(usuarioAux, "proprietario");
				}
			}
		}

		return false;
	}

	public <T> Permissao obtemPermissao(Usuario recebedor,
										T objeto,
										String atributo,
										Tipo tipo) {

		try {
			List<Permissao> resultadosPermissao = manager	.createQuery(
																			"SELECT p FROM " + objeto.getClass().getSimpleName() + " o "
																					+ "INNER JOIN o.permissoes p "
																					+ "WHERE o.id = :id_objeto "
																					+ "AND p.recebedor.id = :id_recebedor "
																					+ "AND (p.atributo = :atributo OR (:atributo IS NULL AND p.atributo IS NULL)) "
																					+ "AND p.tipo = :tipo",
																			Permissao.class)
															.setParameter(	"id_objeto",
																			(Long) new PropertyDescriptor("id", objeto.getClass())	.getReadMethod()
																																	.invoke(objeto))
															.setParameter("id_recebedor", recebedor.getId())
															.setParameter("atributo", atributo)
															.setParameter("tipo", tipo)
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

			if (atribuidor != null) {
				if (this.obtemPermissao(atribuidor, objeto, permissao.getAtributo(), permissao.getTipo()) == null) {
					throw new DataIntegrityViolationException("O solicitante não possui a permissão " + permissao.getTipo() + " do atributo "
							+ permissao.getAtributo() + " da entidade "
							+ objeto.getClass().getName() + ", portanto não pode atribuí-la");
				}
			}

			Usuario recebedor = permissao.getRecebedor() == null ? null : manager.find(Usuario.class, permissao.getRecebedor().getId());
			if (recebedor != null) {
				if (this.obtemPermissao(recebedor, objeto, permissao.getAtributo(), permissao.getTipo()) != null) {
					throw new DataIntegrityViolationException("A permissão " + permissao.getTipo() + " do atributo " + permissao.getAtributo()
							+ " já existe na entidade "
							+ objeto.getClass().getName());
				}

				objeto = (T) manager.find(objeto.getClass(), (Long) new PropertyDescriptor("id", objeto.getClass()).getReadMethod().invoke(objeto));

				if (atribuidor != null) {
					this.obtemPermissoesAtribuidas(atribuidor).add(permissao);
					permissao.setAtribuidor(atribuidor);
				}

				this.obtemPermissoesRecebidas(recebedor).add(permissao);
				permissao.setRecebedor(recebedor);

				((List<Permissao>) new PropertyDescriptor("permissoes", objeto.getClass()).getReadMethod().invoke(objeto)).add(permissao);

				manager.persist(permissao);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
