package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.repository.QuestionRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ReviewRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

public class ProductDetailDto {
  private final Collection<ImageDto> images;
  private final String name;
  private final BigDecimal price;
  private final Collection<PropertyDto> properties;
  private final String description;
  private final ReviewsDto reviews;
  private final Collection<QuestionResponse> questions;

  public ProductDetailDto(
    Product product,
    QuestionRepository questionRepository,
    ReviewRepository reviewRepository
  ) {

    this.images = product.mapImages(ImageDto::new);
    this.name = product.getName();
    this.price = product.getPrice();
    this.properties = product.mapProperties(PropertyDto::new);
    this.description = product.getDescription();
    this.reviews = new ReviewsDto(product, reviewRepository);

    this.questions = questionRepository.streamAllByProduct(product)
      .map(QuestionResponse::new)
      .collect(Collectors.toList());
  }

  public Collection<ImageDto> getImages() {
    return this.images;
  }

  public String getName() {
    return this.name;
  }

  public BigDecimal getPrice() {
    return this.price;
  }

  public Collection<PropertyDto> getProperties() {
    return this.properties;
  }

  public String getDescription() {
    return this.description;
  }

  public ReviewsDto getReviews() {
    return this.reviews;
  }

  public Collection<QuestionResponse> getQuestions() {
    return this.questions;
  }
}
