package datasafer.backup.dao.utility;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import datasafer.backup.dao.PermissaoDao;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Component
public class Modificador {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PermissaoDao permissaoDao;

	@Transactional
	public void insere(	Usuario solicitante,
						Usuario proprietario,
						Object destino,
						String nomeColecao,
						String valor) throws Exception {

		Object objeto = new ObjectMapper().readValue(	valor,
														((Class<?>) ((ParameterizedType) destino.getClass().getDeclaredField(nomeColecao)
																								.getGenericType()).getActualTypeArguments()[0]));
		this.insere(solicitante, proprietario, destino, nomeColecao, objeto);
	}

	// @SuppressWarnings("unchecked")
	@Transactional
	public void insere(	Usuario solicitante,
						Usuario proprietario,
						Object destino,
						String nomeColecao,
						Object objeto) throws Exception {

		if (nomeColecao == null || objeto == null) {
			throw new NullPointerException();
		}

		Set<ConstraintViolation<Object>> errors = Validation.buildDefaultValidatorFactory().getValidator().validate(objeto);
		if (!errors.isEmpty()) {
			throw new ConstraintViolationException(errors);
		}

		solicitante = solicitante == null ? null : manager.find(Usuario.class, solicitante.getId());
		if (solicitante != null && !permissaoDao.temPermissao(solicitante, destino, nomeColecao, Permissao.Tipo.INSERIR)) {
			throw new AccessDeniedException("O solicitante não tem permissão para inserir na colecao " + nomeColecao);
		}

		proprietario = proprietario == null ? null : manager.find(Usuario.class, proprietario.getId());

		for (Field f : objeto.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).unique() == true) {

				Object valor = new PropertyDescriptor(f.getName(), objeto.getClass()).getReadMethod().invoke(objeto);

				CriteriaBuilder builder = manager.getCriteriaBuilder();
				CriteriaQuery<?> criteria = builder.createQuery(f.getType());
				Root<?> root = criteria.from(objeto.getClass());
				criteria.where(builder.equal(root.get(f.getName()), valor));

				/* select(root.get(f.getName())) */

				if (!manager.createQuery(criteria).getResultList().isEmpty()) {
					throw new DataIntegrityViolationException("Já existe " + objeto.getClass().getSimpleName().toLowerCase() + " com " + valor.toString()
							+ " como "
							+ f.getName());
				}
			}
		}

		if (destino != null) {
			try {
				Field atributo = destino.getClass().getDeclaredField(nomeColecao);
				if (Collection.class.isAssignableFrom(atributo.getType()) && !atributo.isAnnotationPresent(Transient.class)) {

					destino = manager.find(destino.getClass(), (Long) new PropertyDescriptor("id", destino.getClass()).getReadMethod().invoke(destino));

					for (Field f : objeto.getClass().getDeclaredFields()) {
						if (f.getType().isAnnotationPresent(Entity.class)) {
							Object entidade = new PropertyDescriptor(f.getName(), objeto.getClass()).getReadMethod().invoke(objeto);
							if (entidade != null) {
								entidade = manager.find(entidade.getClass(),
														(Long) new PropertyDescriptor("id", entidade.getClass()).getReadMethod().invoke(entidade));
								new PropertyDescriptor(f.getName(), objeto.getClass()).getWriteMethod().invoke(objeto, entidade);
							}
						}
					}

					// ((Collection<Object>) new PropertyDescriptor(nomeColecao,
					// destino.getClass()).getReadMethod().invoke(destino)).add(objeto);
					if (atributo.isAnnotationPresent(OneToMany.class)) {
						new PropertyDescriptor(atributo.getAnnotation(OneToMany.class).mappedBy(), objeto.getClass()).getWriteMethod().invoke(objeto, destino);
					}

					if (proprietario != null) {
						new PropertyDescriptor("proprietario", objeto.getClass())	.getWriteMethod()
																					.invoke(objeto, proprietario);
					}

					manager.persist(objeto);
				}
			} catch (IllegalAccessException | InvocationTargetException | IntrospectionException | SecurityException e) {
				e.printStackTrace();
			}
		} else {
			manager.persist(objeto);
		}

	}

	@Transactional
	public void modifica(	Usuario solicitante,
							Object destino,
							String valores) throws UnrecognizedPropertyException, NullPointerException, AccessDeniedException, NoSuchFieldException {

		try {
			this.modifica(solicitante, destino, new ObjectMapper().readValue(valores, destino.getClass()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void modifica(	Usuario solicitante,
							Object destino,
							Object origem) throws NullPointerException, AccessDeniedException, NoSuchFieldException {

		if (destino == null || origem == null) {
			throw new NullPointerException();
		}

		Set<ConstraintViolation<Object>> errors = Validation.buildDefaultValidatorFactory().getValidator().validate(origem);
		if (!errors.isEmpty()) {
			throw new ConstraintViolationException(errors);
		}

		try {

			solicitante = solicitante == null ? null : manager.find(Usuario.class, solicitante.getId());
			destino = manager.find(destino.getClass(), (Long) new PropertyDescriptor("id", destino.getClass()).getReadMethod().invoke(destino));

			List<Registro> registros = new ArrayList<>();
			for (Field f : destino.getClass().getDeclaredFields()) {

				if (f.isAnnotationPresent(JsonIgnore.class) && f.getAnnotation(JsonIgnore.class).value() == true) {
					continue;
				}

				if (f.isAnnotationPresent(Transient.class)) {
					continue;
				}

				JsonProperty property = f.getAnnotation(JsonProperty.class);

				Object valorDestino = new PropertyDescriptor(f.getName(), destino.getClass()).getReadMethod().invoke(destino);
				Object valorOrigem = new PropertyDescriptor(f.getName(), origem.getClass()).getReadMethod().invoke(origem);
				if (valorOrigem != null && !valorOrigem.equals(valorDestino)) {

					if (solicitante != null && !permissaoDao.temPermissao(solicitante, destino, f.getName(), Permissao.Tipo.EDITAR)) {
						throw new AccessDeniedException("O solicitante não tem permissão para editar o atributo " + f.getName());
					}

					if (f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).unique() == true) {

						CriteriaBuilder builder = manager.getCriteriaBuilder();
						CriteriaQuery<?> criteria = builder.createQuery(f.getType());
						Root<?> root = criteria.from(origem.getClass());
						criteria.where(builder.equal(root.get(f.getName()), valorOrigem));

						if (!manager.createQuery(criteria).getResultList().isEmpty()) {
							throw new DataIntegrityViolationException("Já existe " + destino.getClass().getSimpleName().toLowerCase() + " com "
									+ valorOrigem.toString() + " como " + f.getName());
						}
					}

					registros.add(new Registro(	solicitante,
												Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)),
												property != null && !property.value().isEmpty() ? property.value() : f.getName(),
												valorDestino == null	? null
																		: valorDestino instanceof Date	? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorDestino)
																										: valorDestino.toString(),
												valorOrigem == null	? null
																	: valorOrigem instanceof Date	? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorOrigem)
																									: valorOrigem.toString()));

					new PropertyDescriptor(f.getName(), destino.getClass()).getWriteMethod().invoke(destino, valorOrigem);
				}
			}

			((List<Registro>) new PropertyDescriptor("registros", destino.getClass()).getReadMethod().invoke(destino)).addAll(registros);

			manager.persist(destino);

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			e.printStackTrace();
		}
	}
}
