package datasafer.backup.bo.helper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Column;

import org.springframework.dao.DataIntegrityViolationException;

public class Validador {

	public static <T> void validar(T objeto) throws NullPointerException, DataIntegrityViolationException {

		if (objeto == null) {
			throw new NullPointerException();
		}

		for (Field f : objeto	.getClass()
								.getFields()) {
			if (f.isAnnotationPresent(Column.class)) {
				try {
					Column column = f.getAnnotation(Column.class);
					Method method = new PropertyDescriptor(f.getName(), objeto.getClass()).getReadMethod();

					if (!column.nullable() && method.invoke(objeto) == null) {
						throw new DataIntegrityViolationException("A propriedade \"" + f.getName() + "\" não pode ser nula.");

					}
					if (method.getReturnType() == String.class) {
						String value = (String) method.invoke(objeto);
						if (value.length() > column.length()) {
							throw new DataIntegrityViolationException(
									"A propriedade \"" + f.getName() + "\" deve ter comprimento menor ou igual a " + column.length() + ".");
						}
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
					e.printStackTrace();
					continue;
				}

			}
		}
	}

}
