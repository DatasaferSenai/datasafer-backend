package datasafer.backup.dao.utility;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.springframework.dao.DataIntegrityViolationException;

public class Validador {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Validar {

		enum Tipo {
			INDEFINIDO,
			TEXTO,
			SENHA,
			EMAIL,
			NUMERO,
			DATA
		}

		public boolean value() default true;

		public Tipo tipo() default Tipo.INDEFINIDO;

		public boolean anulavel() default false;

		public int comprimentoMinimo() default 0;

		public int comprimentoMaximo() default 0;

		public int valorMinimo() default Integer.MIN_VALUE;

		public int valorMaximo() default Integer.MAX_VALUE;
	}

	public static <T> void validar(T objeto) throws DataIntegrityViolationException {

		for (Field f : objeto	.getClass()
								.getDeclaredFields()) {

			try {
				if (f.isAnnotationPresent(Validar.class)) {
					Validar validacao = f.getAnnotation(Validar.class);
					Object valor = new PropertyDescriptor(f.getName(), objeto.getClass()).getReadMethod().invoke(objeto);

					if (!validacao.anulavel() && (valor == null)) {
						throw new DataIntegrityViolationException("A propriedade " + f.getName() + " não pode ser nula");

					}

					if (valor instanceof String) {
						if (((String) valor).length() < validacao.comprimentoMinimo()) {
							throw new DataIntegrityViolationException(
																		"A propriedade " + f.getName() + " deve ter comprimento de ao menos "
																				+ validacao.comprimentoMaximo() + " caracteres");
						}
						if ((validacao.comprimentoMaximo() > 0 && ((String) valor).length() > validacao.comprimentoMaximo())) {
							throw new DataIntegrityViolationException(
																		"A propriedade " + f.getName() + " deve ter comprimento menor ou igual a "
																				+ validacao.comprimentoMaximo() + " caracteres");

						}
					}

					if (valor instanceof Integer) {
						if (((Integer) valor) < validacao.valorMinimo()) {
							throw new DataIntegrityViolationException(
																		"A propriedade " + f.getName() + " deve ter valor de ao menos "
																				+ validacao.valorMinimo());
						}
						if (((Integer) valor) > validacao.valorMaximo()) {
							throw new DataIntegrityViolationException(
																		"A propriedade " + f.getName() + " deve ter valor menor ou igual a "
																				+ validacao.valorMaximo());

						}
					}

				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}
