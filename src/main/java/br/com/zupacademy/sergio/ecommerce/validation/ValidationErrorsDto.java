package br.com.zupacademy.sergio.ecommerce.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Stream;

public class ValidationErrorsDto {

  @JsonProperty
  private final Stream<FieldErrorDto> fieldErrors;

  public ValidationErrorsDto(List<FieldError> fieldErrors) {
    this.fieldErrors = fieldErrors.stream().map(FieldErrorDto::new);
  }

}
