package datasafer.backup.dao.utility;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import datasafer.backup.dao.PermissaoDao;
import datasafer.backup.dao.utility.annotations.FormulaHql;
import datasafer.backup.dao.utility.annotations.Indireto;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Usuario;

@Repository
public class Carregador {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PermissaoDao permissaoDao;

	@Transactional
	public <T> T obtemEntidade(	Class<T> classe,
								Object... valores) throws NoSuchFieldException {

		if (valores.length % 2 != 0) {
			throw new IllegalArgumentException();
		}

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(classe);
		Root<T> root = criteria.from(classe);
		List<Predicate> predicados = new LinkedList<Predicate>();

		for (int i = 0; i < valores.length; i += 2) {
			predicados.add(builder.equal(root.get(classe.getDeclaredField((String) valores[i]).getName()), valores[i + 1]));
		}

		criteria.where(predicados.toArray(new Predicate[] {}));
		List<T> resultados = manager.createQuery(criteria).getResultList();

		return resultados.isEmpty() ? null : resultados.get(0);
	}

	@Transactional
	public Object obtemAtributo(Usuario solicitante,
								Object objeto,
								String nomeAtributo) throws NoSuchFieldException, AccessDeniedException, IllegalArgumentException {
		return this.obtemAtributo(solicitante, objeto, nomeAtributo, Object.class);
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public <T> T obtemAtributo(	Usuario solicitante,
								Object objeto,
								String nomeAtributo,
								Class<T> tipo) throws NoSuchFieldException, AccessDeniedException, IllegalArgumentException {

		solicitante = solicitante == null ? null : manager.find(Usuario.class, solicitante.getId());
		if (solicitante != null && !permissaoDao.temPermissao(solicitante, objeto, nomeAtributo, Permissao.Tipo.VISUALIZAR)) {
			throw new AccessDeniedException("O solicitante não tem permissão para visualizar o atributo " + nomeAtributo);
		}

		try {
			Field atributo = objeto.getClass().getDeclaredField(nomeAtributo);
			if (!tipo.isAssignableFrom(atributo.getType())) {
				throw new IllegalArgumentException("Tipo inválido");
			}

			List<?> resultados = null;

			if (!atributo.isAnnotationPresent(Transient.class)) {

				CriteriaBuilder builder = manager.getCriteriaBuilder();
				CriteriaQuery<?> criteria = builder.createQuery();
				Root<?> root = criteria.from(objeto.getClass());
				criteria.select(root.get(atributo.getName()))
						.where(builder.equal(root.get("id"), new PropertyDescriptor("id", objeto.getClass()).getReadMethod()
																											.invoke(objeto)));
				resultados = manager.createQuery(criteria).getResultList();

			} else if (atributo.isAnnotationPresent(FormulaHql.class)) {

				FormulaHql hql = atributo.getAnnotation(FormulaHql.class);
				if (!hql.formula().isEmpty() && !hql.identificador().isEmpty()) {
					resultados = manager.createQuery(hql.formula())
										.setParameter(hql.identificador(), new PropertyDescriptor(hql.identificador(), objeto.getClass())	.getReadMethod()
																																			.invoke(objeto))
										.getResultList();
				}
			} else if (atributo.isAnnotationPresent(Indireto.class)) {
				Indireto indireto = atributo.getAnnotation(Indireto.class);
				if (!indireto.atributo().isEmpty() && !indireto.identificador().isEmpty()) {
					Field atributoIndireto = objeto.getClass().getDeclaredField(indireto.atributo());

					CriteriaBuilder builder = manager.getCriteriaBuilder();
					CriteriaQuery<?> criteria = builder.createQuery(atributoIndireto.getType());
					Root<?> root = criteria.from(objeto.getClass());
					criteria.select(root.get(atributoIndireto.getName()).get(indireto.identificador()))
							.where(builder.equal(root.get("id"), new PropertyDescriptor("id", objeto.getClass()).getReadMethod()
																												.invoke(objeto)));
					resultados = manager.createQuery(criteria).getResultList();
				}
			}

			if (resultados != null) {

				if (Collection.class.isAssignableFrom(atributo.getType())) {
					return (T) resultados;

				} else if (Map.class.isAssignableFrom(atributo.getType())) {

					Map<Object, Object> map = new HashMap<Object, Object>();
					for (Iterator<?> iterator = resultados.iterator(); iterator.hasNext();) {
						Object obj[] = (Object[]) iterator.next();
						map.put(obj[0], obj[1]);
					}

					return (T) map;

				} else {
					return (T) (resultados.isEmpty() ? null : atributo	.getType()
																		.cast(resultados.get(0)));
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException(nomeAtributo);
	}

	@SuppressWarnings("unchecked")
	public <T> T carregaAtributo(	Usuario solicitante,
									T entidade,
									String nomeAtributo) throws NoSuchFieldException, AccessDeniedException {

		try {
			Field atributo = entidade.getClass().getDeclaredField(nomeAtributo);
			Object valor = this.obtemAtributo(solicitante, entidade, atributo.getName());
			if (valor != null) {
				if (atributo.isAnnotationPresent(Transient.class)) {
					if (Collection.class.isAssignableFrom(valor.getClass())) {
						((Collection<Object>) new PropertyDescriptor(atributo.getName(), entidade.getClass())	.getReadMethod()
																												.invoke(entidade)).addAll((Collection<Object>) valor);
					} else if (Map.class.isAssignableFrom(valor.getClass())) {
						((Map<Object, Object>) new PropertyDescriptor(atributo.getName(), entidade.getClass())	.getReadMethod()
																												.invoke(entidade)).putAll((Map<Object, Object>) valor);
					} else {
						new PropertyDescriptor(atributo.getName(), entidade.getClass()).getWriteMethod().invoke(entidade, valor);
					}
				} else {
					new PropertyDescriptor(atributo.getName(), entidade.getClass()).getWriteMethod().invoke(entidade, valor);
				}
			}

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException | SecurityException e) {
			e.printStackTrace();
		}
		return entidade;
	}

	public <T> List<T> carregaAtributo(	Usuario solicitante,
										List<T> entidades,
										String nome) throws NoSuchFieldException, AccessDeniedException {
		for (T o : entidades) {
			this.carregaAtributo(solicitante, o, nome);
		}
		return entidades;
	}

	public <T> T carregaAtributos(	Usuario solicitante,
									T entidade,
									String[] nomes) throws NoSuchFieldException, AccessDeniedException {
		for (String n : nomes) {
			this.carregaAtributo(solicitante, entidade, n);
		}
		return entidade;
	}

	public <T> List<T> carregaAtributos(Usuario solicitante,
										List<T> entidades,
										String[] nomes) throws NoSuchFieldException, AccessDeniedException {
		for (T o : entidades) {
			this.carregaAtributos(solicitante, o, nomes);
		}
		return entidades;
	}

	public <T> T carregaTransientes(Usuario solicitante,
									T entidade) throws NoSuchFieldException, AccessDeniedException {
		for (Field f : entidade.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Transient.class)) {
				this.carregaAtributo(solicitante, entidade, f.getName());
			}
		}
		return entidade;
	}

	public <T> List<T> carregaTransientes(	Usuario solicitante,
											List<T> objetos) throws NoSuchFieldException, AccessDeniedException {
		for (T o : objetos) {
			this.carregaTransientes(solicitante, o);
		}
		return objetos;
	}

}
