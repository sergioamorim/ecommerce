package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.ReviewRequest;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ReviewRepository;
import br.com.zupacademy.sergio.ecommerce.validation.ForeignKeyExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
public class ReviewController {
  private final ReviewRepository reviewRepository;
  private final ProductRepository productRepository;

  @Autowired
  public ReviewController(
    ReviewRepository reviewRepository, ProductRepository productRepository
  ) {
    this.reviewRepository = reviewRepository;
    this.productRepository = productRepository;
  }

  @PostMapping("/products/{productId}/reviews")
  public ResponseEntity<?> createReview(
    @PathVariable @ForeignKeyExists(domainClass = Product.class) Long productId,
    @RequestBody @Valid ReviewRequest reviewRequest,
    @AuthenticationPrincipal @NotNull User user
  ) {
    this.reviewRepository.save(reviewRequest.toReview(
      user, this.productRepository.findById(productId).orElseThrow()  // ForeignKeyExists validation shall guarantee this exists
    ));
    return ResponseEntity.ok().build();
  }
}
