package datasafer.backup.dao.utility;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

@Repository
public class Carregador {

	@PersistenceContext
	private EntityManager manager;

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FormulaHql {

		public String formula();

		public String identificador();
	}

	public <T> void obtemEntidade(final Class<T> classe,
	                              final Supplier<?> supplier){
		System.out.println();
		
		
	}
	
	public <T> T obtemEntidade(	final Class<T> classe,
								final Object... valores) throws NoSuchFieldException {

		if (valores.length % 2 != 0) {
			throw new IllegalArgumentException();
		}

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(classe);
		Root<T> root = criteria.from(classe);
		for (int i = 0; i < valores.length; i += 2) {
			Field atributo = classe.getDeclaredField((String) valores[i]);
			criteria.where(builder.equal(root.get(atributo.getName()), valores[i + 1]));
		}
		List<T> resultados = manager.createQuery(criteria).getResultList();

		return resultados.isEmpty() ? null : resultados.get(0);
	}

	public <T> Object obtemAtributo(T objeto,
									String nomeAtributo) throws NoSuchFieldException {
		try {
			Field atributo = objeto.getClass().getDeclaredField(nomeAtributo);

			List<?> resultados = null;

			if (!atributo.isAnnotationPresent(Transient.class)) {

				CriteriaBuilder builder = manager.getCriteriaBuilder();
				CriteriaQuery<?> criteria = builder.createQuery(atributo.getType());
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
			}
			if (resultados != null) {
				if (Collection.class.isAssignableFrom(atributo.getType())) {
					return resultados;

				} else if (Map.class.isAssignableFrom(atributo.getType())) {

					Map<Object, Object> map = new HashMap<Object, Object>();
					for (Iterator<?> iterator = resultados.iterator(); iterator.hasNext();) {
						Object obj[] = (Object[]) iterator.next();
						map.put(obj[0], obj[1]);
					}

					return map;

				} else {
					return resultados.isEmpty() ? null : atributo	.getType()
																	.cast(resultados.get(0));
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException(nomeAtributo);
	}

	@SuppressWarnings("unchecked")
	public <T> T carregaAtributo(	final T entidade,
									final String nomeAtributo) throws NoSuchFieldException {

		try {
			Field atributo = entidade.getClass().getDeclaredField(nomeAtributo);
			Object valor = this.obtemAtributo(entidade, atributo.getName());
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

	public <T> List<T> carregaAtributo(	final List<T> entidades,
										final String nome) throws NoSuchFieldException {
		for (T o : entidades) {
			this.carregaAtributo(o, nome);
		}
		return entidades;
	}

	public <T> T carregaAtributos(	final T entidade,
									final String[] nomes) throws NoSuchFieldException {
		for (String n : nomes) {
			this.carregaAtributo(entidade, n);
		}
		return entidade;
	}

	public <T> List<T> carregaAtributos(final List<T> entidades,
										final String[] nomes) throws NoSuchFieldException {
		for (T o : entidades) {
			this.carregaAtributos(o, nomes);
		}
		return entidades;
	}

	public <T> T carregaTransientes(final T entidade) throws NoSuchFieldException {
		for (Field f : entidade.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Transient.class) && f.isAnnotationPresent(FormulaHql.class)) {
				this.carregaAtributo(entidade, f.getName());
			}
		}
		return entidade;
	}

	public <T> List<T> carregaTransientes(final List<T> objetos) throws NoSuchFieldException {
		for (T o : objetos) {
			this.carregaTransientes(o);
		}
		return objetos;
	}

}
