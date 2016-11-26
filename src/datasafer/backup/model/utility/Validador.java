package datasafer.backup.model.utility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

public class Validador {

	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
	@Constraint(validatedBy = {})
	@Target({	ElementType.METHOD,
				ElementType.FIELD,
				ElementType.ANNOTATION_TYPE,
				ElementType.CONSTRUCTOR,
				ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Email {

		String message() default "Email inválido";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")
	@Constraint(validatedBy = {})
	@Target({	ElementType.METHOD,
				ElementType.FIELD,
				ElementType.ANNOTATION_TYPE,
				ElementType.CONSTRUCTOR,
				ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Senha {

		String message() default "Senha inválida. Deve conter ao menos um número, uma letra minúscula e uma letra maíuscula, não pode conter espaços e deve ter no mínimo 8 caracteres";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

}
