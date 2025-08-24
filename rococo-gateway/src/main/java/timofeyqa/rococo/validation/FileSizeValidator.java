package timofeyqa.rococo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

public class FileSizeValidator implements ConstraintValidator<FileSize, String> {

  private long maxBytes;

  @Override
  public void initialize(FileSize constraintAnnotation) {
    this.maxBytes = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(String base64, ConstraintValidatorContext context) {
    if (StringUtils.isEmpty(base64)) {
      return true;
    }
    try {
      String pureBase64 = base64;
      int commaIndex = base64.indexOf(',');
      if (commaIndex > 0) {
        pureBase64 = base64.substring(commaIndex + 1);
      }

      pureBase64 = pureBase64.replaceAll("\\s+", "");

      byte[] decoded = Base64.getDecoder().decode(pureBase64);

      return decoded.length <= maxBytes;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}