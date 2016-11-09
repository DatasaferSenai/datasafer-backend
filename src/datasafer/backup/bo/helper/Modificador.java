package datasafer.backup.bo.helper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Modificador {

	public static <T> void modificar(	T objeto,
										Map<String, Object> valores)
			throws NullPointerException, DataIntegrityViolationException {

		if (objeto == null || valores == null) {
			throw new NullPointerException();
		}

		for (Field f : objeto	.getClass()
								.getFields()) {
			if (!f.isAnnotationPresent(JsonIgnore.class) && valores.containsKey(f.getName())) {
				try {

					Object valor = valores.get(f.getName());
					new PropertyDescriptor(f.getName(), objeto.getClass())	.getWriteMethod()
																			.invoke(objeto, valor);

				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
					e.printStackTrace();
					continue;
				}

			}
		}
	}

}
