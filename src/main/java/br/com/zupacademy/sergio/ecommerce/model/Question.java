package br.com.zupacademy.sergio.ecommerce.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class Question {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String title;

  @ManyToOne(optional = false)
  private Product product;

  @ManyToOne(optional = false)
  private User user;

  @CreationTimestamp
  private ZonedDateTime creation;

  public Question(String title, Product product, User user) {
    this.title = title;
    this.product = product;
    this.user = user;
  }

  @Deprecated
  protected Question() {
  }

  public String getTitle() {
    return this.title;
  }

  public String getProductUserEmail() {
    return this.product.getUserEmail();
  }

  public String getUserEmail() {
    return this.user.getEmail();
  }

  public String getProductName() {
    return this.product.getName();
  }
}
