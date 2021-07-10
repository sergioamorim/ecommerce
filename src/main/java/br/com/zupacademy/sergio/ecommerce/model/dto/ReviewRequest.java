package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.Review;
import br.com.zupacademy.sergio.ecommerce.model.User;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReviewRequest {

  @NotNull
  @Range(min = 1, max = 5)
  private final Integer rating;

  @NotBlank
  private final String title;

  @NotBlank
  @Length(max = 500)
  private final String description;

  public ReviewRequest(Integer rating, String title, String description) {
    this.rating = rating;
    this.title = title;
    this.description = description;
  }

  public Review toReview(User user, Product product) {
    return new Review(
      this.rating,
      this.title,
      this.description,
      user,
      product
    );
  }
}
