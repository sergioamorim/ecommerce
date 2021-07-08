package br.com.zupacademy.sergio.ecommerce.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ValidationErrorsDto {

  @JsonProperty
  private final Stream<FieldErrorDto> fieldErrors;

  public ValidationErrorsDto(List<FieldError> fieldErrors) {
    this.fieldErrors = fieldErrors.stream().map(FieldErrorDto::new);
  }

  public ValidationErrorsDto(Set<ConstraintViolation<?>> constraintViolations) {
    this.fieldErrors = constraintViolations.stream().map(FieldErrorDto::new);
  }

}
