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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;

import datasafer.backup.model.Registro;
import datasafer.backup.model.Usuario;

public class Modificador {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Modificavel {

		public boolean value()

		default true;

		public boolean autoModificavel() default true;

	}

	public static <T> Set<Registro> modifica(	Usuario solicitante,
												T destino,
												T origem) throws AccessDeniedException {

		Set<Registro> registros = new HashSet<Registro>();
		for (Field f : destino	.getClass()
								.getDeclaredFields()) {
			try {
				if (f.isAnnotationPresent(Modificavel.class)) {
					Modificavel modificavel = f.getAnnotation(Modificavel.class);

					if (destino instanceof Usuario && solicitante != null && ((Usuario) destino).getLogin().equals(solicitante.getLogin())
							&& !modificavel.autoModificavel()) {
						throw new AccessDeniedException("A propriedade " + f.getName() + " não é auto modificável");
					}

					if (modificavel.value() == true) {

						Object valorDestino = new PropertyDescriptor(f.getName(), destino.getClass()).getReadMethod().invoke(destino);

						if (origem != null) {
							Object valorOrigem = new PropertyDescriptor(f.getName(), origem.getClass()).getReadMethod().invoke(origem);

							if (valorOrigem != null && !valorOrigem.equals(valorDestino)) {

								Registro registro = new Registro();
								registro.setSolicitante(solicitante);
								registro.setData(Timestamp.from(LocalDateTime	.now()
																				.atZone(ZoneId.systemDefault())
																				.toInstant()));
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
							Registro registro = new Registro();
							registro.setSolicitante(solicitante);
							registro.setData(Timestamp.from(LocalDateTime	.now()
																			.atZone(ZoneId.systemDefault())
																			.toInstant()));
							registro.setAtributo(f.getName());

							registro.setDe(null);
							registro.setPara(valorDestino == null	? null
																	: valorDestino instanceof Date	? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorDestino)
																									: valorDestino.toString());
							registros.add(registro);
						}
					}
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
				e.printStackTrace();
				continue;
			}
		}

		return registros;
	}

}
