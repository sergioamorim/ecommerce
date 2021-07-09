package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.ProductRequest;
import br.com.zupacademy.sergio.ecommerce.model.dto.ProductResponse;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductPropertyRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.validation.ProductPropertyNameDuplicationValidator;
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
  private final ProductPropertyRepository productPropertyRepository;
  private final CategoryRepository categoryRepository;

  @Autowired
  public ProductController(
    ProductRepository productRepository,
    ProductPropertyRepository productPropertyRepository,
    CategoryRepository categoryRepository
  ) {

    this.productRepository = productRepository;
    this.productPropertyRepository = productPropertyRepository;
    this.categoryRepository = categoryRepository;

  }

  @InitBinder
  private void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new ProductPropertyNameDuplicationValidator());
  }

  @PostMapping(value = "/products")
  public ResponseEntity<ProductResponse> createProduct(
    @RequestBody @Valid ProductRequest productRequest,
    @AuthenticationPrincipal @NotNull User user
  ) {
    return ResponseEntity.ok(new ProductResponse(productRepository.save(
      productRequest.toProduct(
        this.productPropertyRepository,
        this.categoryRepository,
        user
      )
    )));

  }

}
