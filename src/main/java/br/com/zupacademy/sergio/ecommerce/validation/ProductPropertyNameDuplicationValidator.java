package br.com.zupacademy.sergio.ecommerce.validation;

import br.com.zupacademy.sergio.ecommerce.model.dto.ProductRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ProductPropertyNameDuplicationValidator implements Validator {
  @Override
  public boolean supports(Class<?> aClass) {
    return aClass.isAssignableFrom(ProductRequest.class);
  }

  @Override
  public void validate(Object object, Errors errors) {

    if (((ProductRequest) object).hasPropertiesWithTheSameName()) {
      errors.rejectValue(
        "productProperties",
        "ProductPropertyWithDuplicateName",
        "must have different names"
      );
    }

  }
}
