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

	public <T> Object obtemAtributo(T objeto,
									String nome) throws IllegalArgumentException {
		try {
			Field atributo = null;
			for (Field f : objeto.getClass().getDeclaredFields()) {
				if (f.getName().equals(nome)) {
					atributo = f;
					break;
				}
			}
			if (atributo != null) {

				List<?> resultados = null;

				if (!atributo.isAnnotationPresent(Transient.class)) {

					CriteriaBuilder builder = manager.getCriteriaBuilder();
					CriteriaQuery<?> criteria = builder.createQuery(atributo.getType());
					Root<?> root = criteria.from(objeto.getClass());
					criteria.select(root.get(atributo.getName()));
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
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}

	@SuppressWarnings("unchecked")
	public <T> T carregaAtributo(	T objeto,
									String nome) {

		try {
			Object valor = this.obtemAtributo(objeto, nome);
			if (valor != null) {
				if (Collection.class.isAssignableFrom(valor.getClass())) {
					((Collection<Object>) new PropertyDescriptor(nome, objeto.getClass()).getReadMethod().invoke(objeto)).addAll((Collection<Object>) valor);
				} else if (Map.class.isAssignableFrom(valor.getClass())) {
					((Map<Object, Object>) new PropertyDescriptor(nome, objeto.getClass()).getReadMethod().invoke(objeto)).putAll((Map<Object, Object>) valor);
				} else {
					new PropertyDescriptor(nome, objeto.getClass()).getWriteMethod().invoke(objeto, valor);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			e.printStackTrace();
		}
		return objeto;
	}

	public <T> List<T> carregaAtributo(	List<T> objetos,
										String nome) {
		for (T o : objetos) {
			this.carregaAtributo(o, nome);
		}
		return objetos;
	}

	public <T> T carregaAtributos(	T objeto,
									String[] nomes) {
		for (String n : nomes) {
			this.carregaAtributo(objeto, n);
		}
		return objeto;
	}

	public <T> List<T> carregaAtributos(List<T> objetos,
										String[] nomes) {
		for (T o : objetos) {
			this.carregaAtributos(o, nomes);
		}
		return objetos;
	}

	public <T> T carregaTransientes(T objeto) {
		for (Field f : objeto.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Transient.class) && f.isAnnotationPresent(FormulaHql.class)) {
				this.carregaAtributo(objeto, f.getName());
			}
		}
		return objeto;
	}

	public <T> List<T> carregaTransientes(List<T> objetos) {
		for (T o : objetos) {
			this.carregaTransientes(o);
		}
		return objetos;
	}

}
