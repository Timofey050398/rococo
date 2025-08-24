package timofeyqa.rococo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;

@Component
public class PageableSizeValidator implements ConstraintValidator<SizeLimited, Pageable> {

  @Autowired
  private RococoGatewayServiceConfig config;

  private int max;

  @Override
  public void initialize(SizeLimited constraintAnnotation) {
    this.max = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(Pageable pageable, ConstraintValidatorContext context) {
    if (pageable == null) {
      return true;
    }
    int effectiveMax = (max == -1) ? config.getPageMaxSize() : max;
    if (pageable.getPageSize() > effectiveMax) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(
          "Page size can't be greater than " + effectiveMax
      ).addConstraintViolation();
      return false;
    }

    return true;
  }
}
