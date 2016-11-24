package datasafer.backup.dao.utility;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import datasafer.backup.dao.PermissaoDao;
import datasafer.backup.dao.UsuarioDao;
import datasafer.backup.model.Permissao;
import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

@Component
public class Modificador {

	@Autowired
	private PermissaoDao permissaoDao;
	@Autowired
	private UsuarioDao usuarioDao;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Modificavel {

		public boolean value()

		default true;

		public boolean autoModificavel() default true;

	}

	public List<Registro> modifica(	Usuario solicitante,
									Usuario destino,
									Usuario origem) throws AccessDeniedException {

		Usuario superior = usuarioDao.obtemSuperior(destino);
		destino.setLoginSuperior(superior == null ? null : superior.getLogin());

		List<Registro> registros = new ArrayList<>();
		for (Field f : destino.getClass().getDeclaredFields()) {
			try {
				JsonIgnore ignore = f.getAnnotation(JsonIgnore.class);
				if (ignore != null && ignore.value() == true) {
					continue;
				}

				JsonProperty property = f.getAnnotation(JsonProperty.class);
				if (property != null && property.access().equals(Access.READ_ONLY)) {
					continue;
				}

				if (solicitante != null) {
					Permissao permissao = permissaoDao.resolvePermissao(solicitante, destino, f.getName(), Permissao.Tipo.EDITAR);
					if (permissao == null || !permissao.isPermitido()) {
						throw new AccessDeniedException("O solicitante não tem permissão para editar o atributo " + f.getName());
					}
				}

				Object valorDestino = new PropertyDescriptor(f.getName(), destino.getClass()).getReadMethod().invoke(destino);
				if (origem != null) {
					Object valorOrigem = new PropertyDescriptor(f.getName(), origem.getClass()).getReadMethod().invoke(origem);

					if (valorOrigem != null && !valorOrigem.equals(valorDestino)) {

						Registro registro = new Registro();
						registro.setSolicitante(solicitante);
						registro.setData(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
						registro.setAtributo(f.getName());

						registro.setDe(valorDestino == null	? null
															: valorDestino instanceof Date	? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorDestino)
																							: valorDestino.toString());
						registro.setPara(valorOrigem == null	? null
																: valorOrigem instanceof Date	? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorOrigem)
																								: valorOrigem.toString());
						registros.add(registro);

						new PropertyDescriptor(f.getName(), destino.getClass()).getWriteMethod().invoke(destino, valorOrigem);
					}
				} else {
					registros.add(new Registro(	solicitante,
												Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
												f.getName(),
												null,
												valorDestino == null	? null
																		: valorDestino instanceof Date	? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorDestino)
																										: valorDestino.toString()));
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
				e.printStackTrace();
				continue;
			}
		}

		return registros;
	}

	public <T> List<Registro> modifica(	Usuario solicitante,
										T destino,
										T origem) throws AccessDeniedException {

		// Permissao permissaoGeral = permissaoDao.obtemPermissao(solicitante,
		// destino, null, Permissao.Tipo.EDITAR);

		List<Registro> registros = new ArrayList<>();
		// for (Field f : destino.getClass().getDeclaredFields()) {
		// try {
		//
		// Permissao permissaoEspecifica =
		// permissaoDao.obtemPermissao(solicitante, destino, f.getName(),
		// Permissao.Tipo.EDITAR);
		//
		// if ((permissaoEspecifica != null &&
		// !permissaoEspecifica.isPermitido()) || (permissaoEspecifica == null
		// && !permissaoGeral.isPermitido())) {
		// throw new AccessDeniedException("O solicitante não tem permissão para
		// editar o atributo " + f.getName());
		// }
		//
		// Object valorDestino = new PropertyDescriptor(f.getName(),
		// destino.getClass()).getReadMethod().invoke(destino);
		//
		// if (origem != null) {
		// Object valorOrigem = new PropertyDescriptor(f.getName(),
		// origem.getClass()).getReadMethod().invoke(origem);
		//
		// if (valorOrigem != null && !valorOrigem.equals(valorDestino)) {
		//
		// Registro registro = new Registro();
		// registro.setSolicitante(solicitante);
		// registro.setData(Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		// registro.setAtributo(f.getName());
		//
		// registro.setDe(valorDestino == null ? null
		// : valorDestino instanceof Date ? new SimpleDateFormat("dd/MM/yyyy
		// HH:mm:ss").format(valorDestino)
		// : valorDestino.toString());
		// registro.setPara(valorOrigem == null ? null
		// : valorOrigem instanceof Date ? new SimpleDateFormat("dd/MM/yyyy
		// HH:mm:ss").format(valorOrigem)
		// : valorOrigem.toString());
		// registros.add(registro);
		//
		// new PropertyDescriptor(f.getName(),
		// destino.getClass()).getWriteMethod().invoke(destino, valorOrigem);
		// }
		// } else {
		// registros.add(new Registro( solicitante,
		// Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
		// f.getName(),
		// null,
		// valorDestino == null ? null
		// : valorDestino instanceof Date ? new SimpleDateFormat("dd/MM/yyyy
		// HH:mm:ss").format(valorDestino)
		// : valorDestino.toString()));
		// }
		//
		// } catch (IllegalAccessException | IllegalArgumentException |
		// InvocationTargetException | IntrospectionException e) {
		// e.printStackTrace();
		// continue;
		// }
		// }

		return registros;
	}

}
