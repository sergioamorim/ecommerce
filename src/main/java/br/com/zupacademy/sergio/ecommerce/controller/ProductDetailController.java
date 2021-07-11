package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.dto.ProductDetailDto;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.QuestionRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Optional;

@RestController
public class ProductDetailController {

  private final ProductRepository productRepository;
  private final QuestionRepository questionRepository;
  private final ReviewRepository reviewRepository;

  @Autowired
  public ProductDetailController(
    ProductRepository productRepository,
    QuestionRepository questionRepository,
    ReviewRepository reviewRepository
  ) {
    this.productRepository = productRepository;
    this.questionRepository = questionRepository;
    this.reviewRepository = reviewRepository;
  }

  @Transactional
  @GetMapping("/products/{productId}")
  public ResponseEntity<ProductDetailDto> readProduct(
    @PathVariable Long productId
  ) {

    return this.productDetailOrNotFound(
      this.productRepository.findById(productId)
    );

  }

  private ResponseEntity<ProductDetailDto> productDetailOrNotFound(
    Optional<Product> queriedProduct
  ) {

    return queriedProduct
      .map(product -> ResponseEntity.ok(new ProductDetailDto(
        product, this.questionRepository, this.reviewRepository
      )))
      .orElse(ResponseEntity.notFound().build());

  }
}
