package datasafer.backup.dao.helper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Modificador {

	public static <T> void modificar(	T destino,
										T origem)
			throws NullPointerException, UnsupportedOperationException {

		if (destino == null || origem == null) {
			throw new NullPointerException();
		}

		if (destino.getClass() != origem.getClass()) {
			throw new UnsupportedOperationException();
		}

		for (Field f : destino	.getClass()
								.getDeclaredFields()) {

			try {

				Object valor = new PropertyDescriptor(f.getName(), origem.getClass())	.getReadMethod()
																						.invoke(origem);

				if (valor != null) {
					new PropertyDescriptor(f.getName(), destino.getClass())	.getWriteMethod()
																			.invoke(destino, valor);
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}
