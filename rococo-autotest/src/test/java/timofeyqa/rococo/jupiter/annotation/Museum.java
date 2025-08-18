package timofeyqa.rococo.jupiter.annotation;

import timofeyqa.rococo.data.entity.Country;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Museum {
  String title() default "";
  String description() default "";
  String photo() default "";
  String city() default "";
  Country country() default Country.RUSSIA;
}
