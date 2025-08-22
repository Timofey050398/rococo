package timofeyqa.rococo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;

public class PageableSizeValidator implements ConstraintValidator<SizeLimited, Pageable> {

  private final RococoGatewayServiceConfig config;

  @Autowired
  public PageableSizeValidator(RococoGatewayServiceConfig config) {
    this.config = config;
  }

  @Override
  public boolean isValid(Pageable pageable, ConstraintValidatorContext context) {
    if (pageable == null) {
      return true;
    }
    return pageable.getPageSize() <= config.getPageMaxSize();
  }
}
