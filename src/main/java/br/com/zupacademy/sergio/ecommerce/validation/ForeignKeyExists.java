package br.com.zupacademy.sergio.ecommerce.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {ForeignKeyExistsValidator.class})
public @interface ForeignKeyExists {
  Class<?> domainClass();

  boolean nullable() default true;

  String message() default "id doesn't exist";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
