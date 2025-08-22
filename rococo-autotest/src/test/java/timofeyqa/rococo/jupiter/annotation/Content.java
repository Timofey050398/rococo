package timofeyqa.rococo.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Content {

  Artist[] artists()  default {};
  Museum[] museums() default {};
  Painting[] paintings() default {};
  int artistCount() default 0;
  int museumCount() default 0;
  int paintingCount() default 0;
}
