package br.com.zupacademy.sergio.ecommerce;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllersExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ValidationErrorsDto handleMethodArgumentNotValidException(
    MethodArgumentNotValidException methodArgumentNotValidException
  ) {

    return new ValidationErrorsDto(
      methodArgumentNotValidException.getFieldErrors()
    );
  }

}
