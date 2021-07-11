package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.repository.ReviewRepository;

import java.util.Collection;
import java.util.stream.Collectors;

public class ReviewsDto {
  private final Collection<ReviewResponse> reviews;
  private final Integer reviewCount;
  private final Double ratingAverage;

  public ReviewsDto(Product product, ReviewRepository reviewRepository) {
    this.reviews = reviewRepository.streamAllByProduct(product)
      .map(ReviewResponse::new)
      .collect(Collectors.toList());

    this.ratingAverage = this.reviews.stream()
      .mapToInt(ReviewResponse::getRating)
      .average()
      .orElse(0);

    this.reviewCount = this.reviews.size();
  }

  public Collection<ReviewResponse> getReviews() {
    return this.reviews;
  }

  public Integer getReviewCount() {
    return this.reviewCount;
  }

  public Double getRatingAverage() {
    return this.ratingAverage;
  }
}
