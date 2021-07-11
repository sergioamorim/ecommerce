package br.com.zupacademy.sergio.ecommerce.model;

import javax.persistence.*;

@Entity
public class Review {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private Integer rating;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  @ManyToOne(optional = false)
  private User user;

  @ManyToOne(optional = false)
  private Product product;

  public Review(
    Integer rating,
    String title,
    String description,
    User user,
    Product product
  ) {
    this.rating = rating;
    this.title = title;
    this.description = description;
    this.user = user;
    this.product = product;
  }

  @Deprecated
  protected Review() {
  }

  public String getUserEmail() {
    return this.user.getEmail();
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
}
