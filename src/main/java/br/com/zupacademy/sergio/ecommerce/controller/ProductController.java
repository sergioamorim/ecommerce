package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.ProductRequest;
import br.com.zupacademy.sergio.ecommerce.model.dto.ProductResponse;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.PropertyRepository;
import br.com.zupacademy.sergio.ecommerce.validation.PropertyNameDuplicationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
public class ProductController {
  private final ProductRepository productRepository;
  private final PropertyRepository propertyRepository;
  private final CategoryRepository categoryRepository;

  @Autowired
  public ProductController(
    ProductRepository productRepository,
    PropertyRepository propertyRepository,
    CategoryRepository categoryRepository
  ) {

    this.productRepository = productRepository;
    this.propertyRepository = propertyRepository;
    this.categoryRepository = categoryRepository;

  }

  @InitBinder
  private void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new PropertyNameDuplicationValidator());
  }

  @PostMapping("/products")
  public ResponseEntity<ProductResponse> createProduct(
    @RequestBody @Valid ProductRequest productRequest,
    @AuthenticationPrincipal @NotNull User user
  ) {
    return ResponseEntity.ok(new ProductResponse(productRepository.save(
      productRequest.toProduct(
        this.propertyRepository,
        this.categoryRepository,
        user
      )
    )));

  }

}
