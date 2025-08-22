package timofeyqa.rococo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PageableSizeValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface SizeLimited {
  String message() default "Page size can't be greater than 10";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
