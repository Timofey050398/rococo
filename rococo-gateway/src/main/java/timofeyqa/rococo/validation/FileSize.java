package timofeyqa.rococo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileSizeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileSize {
  String message() default "File size exceeds allowed limit";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  long max() default RococoGatewayServiceConfig.ONE_MB;
}
