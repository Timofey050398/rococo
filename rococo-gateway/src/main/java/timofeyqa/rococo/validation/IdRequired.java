package timofeyqa.rococo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IdRequiredValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IdRequired {
  String message() default "Object's id required for this request";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
