package br.com.zupacademy.sergio.ecommerce.validation;

import org.springframework.validation.FieldError;

public class FieldErrorDto {
  private final String name;
  private final String message;

  public FieldErrorDto(FieldError fieldError) {
    this.name = fieldError.getField();
    this.message = fieldError.getDefaultMessage();
  }

  public String getName() {
    return this.name;
  }

  public String getMessage() {
    return this.message;
  }
}
