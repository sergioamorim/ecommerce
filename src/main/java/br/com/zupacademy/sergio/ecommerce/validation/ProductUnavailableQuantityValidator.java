package br.com.zupacademy.sergio.ecommerce.validation;

import br.com.zupacademy.sergio.ecommerce.model.dto.PurchaseRequest;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProductUnavailableQuantityValidator implements Validator {

  private final ProductRepository productRepository;

  @Autowired
  public ProductUnavailableQuantityValidator(
    ProductRepository productRepository
  ) {
    this.productRepository = productRepository;
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return PurchaseRequest.class.isAssignableFrom(aClass);
  }

  @Override
  public void validate(Object object, Errors errors) {
    if (!productHasQuantityAvailable((PurchaseRequest) object)) {
      errors.rejectValue(
        "quantity",
        "ProductUnavailableQuantity",
        "product quantity unavailable"
      );
    }
  }

  private boolean productHasQuantityAvailable(PurchaseRequest purchaseRequest) {
    return this.productRepository.findById(
      purchaseRequest.getProductId()
    ).orElseThrow().getAvailableQuantity() >= purchaseRequest.getQuantity();
  }
}
