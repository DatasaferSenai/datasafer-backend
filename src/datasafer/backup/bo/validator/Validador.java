package datasafer.backup.bo.validator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.Column;

import org.springframework.dao.DataIntegrityViolationException;

public class Validador {

	public static <T> void validar(T objeto) throws DataIntegrityViolationException {

		for (Field f : objeto	.getClass()
								.getFields()) {
			if (f.isAnnotationPresent(Column.class) && !f	.getAnnotation(Column.class)
															.nullable()) {
				try {
					if (new PropertyDescriptor(f.getName(), objeto.getClass())	.getReadMethod()
																				.invoke(objeto) == null) {

						throw new DataIntegrityViolationException("O campo \"" + f.getName() + "\" não pode ser nulo.");

					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

}
