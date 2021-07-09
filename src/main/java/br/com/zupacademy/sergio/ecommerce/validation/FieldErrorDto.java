package br.com.zupacademy.sergio.ecommerce.validation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;

public class FieldErrorDto {
  private final String name;
  private final String message;

  public FieldErrorDto(FieldError fieldError) {
    this.name = fieldError.getField();
    this.message = fieldError.getDefaultMessage();
  }

  public FieldErrorDto(ConstraintViolation<?> constraintViolation) {

    this.name = ((PathImpl) constraintViolation.getPropertyPath())
      .getLeafNode().getName();

    this.message = constraintViolation.getMessage()
      + " (" + constraintViolation.getInvalidValue() + ")";
  }

  public String getName() {
    return this.name;
  }

  public String getMessage() {
    return this.message;
  }
}
