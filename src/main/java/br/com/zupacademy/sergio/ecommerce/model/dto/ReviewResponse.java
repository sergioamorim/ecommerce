package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Review;

public class ReviewResponse {
  private final Integer rating;
  private final String title;
  private final String description;
  private final String userEmail;

  public ReviewResponse(Review review) {
    this.rating = review.getRating();
    this.title = review.getTitle();
    this.description = review.getDescription();
    this.userEmail = review.getUserEmail();
  }

  public Integer getRating() {
    return this.rating;
  }

  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }

  public String getUserEmail() {
    return this.userEmail;
  }
}
