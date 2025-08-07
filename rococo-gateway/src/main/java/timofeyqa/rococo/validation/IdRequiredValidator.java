package timofeyqa.rococo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import timofeyqa.rococo.model.ResponseDto;

public class IdRequiredValidator implements ConstraintValidator<IdRequired, ResponseDto> {

  @Override
  public boolean isValid(ResponseDto responseDto, ConstraintValidatorContext context) {
    return responseDto.id() != null;
  }
}
