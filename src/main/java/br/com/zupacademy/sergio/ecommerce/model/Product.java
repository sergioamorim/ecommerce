package br.com.zupacademy.sergio.ecommerce.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
public class Product {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private User user;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer availableQuantity;

  @OneToMany(cascade = ALL, fetch = EAGER)
  private Set<ProductProperty> productProperties;

  @OneToMany(cascade = ALL, fetch = EAGER, mappedBy = "product")
  private Set<Image> images;

  @Column(nullable = false, length = 1000)
  private String description;

  @ManyToOne(optional = false)
  private Category category;

  @CreationTimestamp
  @Column(nullable = false)
  private ZonedDateTime creation;

  public Product(
    User user,
    String name,
    BigDecimal price,
    Integer availableQuantity,
    Set<ProductProperty> productProperties,
    String description,
    Category category
  ) {
    this.user = user;
    this.name = name;
    this.price = price;
    this.availableQuantity = availableQuantity;
    this.productProperties = productProperties;
    this.description = description;
    this.category = category;
  }

  @Deprecated  // jpa
  protected Product() {
  }

  public Long getId() {
    return this.id;
  }

  public Long getUserId() {
    return this.user.getId();
  }

  public String getUserEmail() {
    return this.user.getEmail();
  }

  public String getName() {
    return this.name;
  }

  public BigDecimal getPrice() {
    return this.price;
  }

  public Integer getAvailableQuantity() {
    return this.availableQuantity;
  }

  public Collection<ProductProperty> getProductProperties() {
    return this.productProperties;
  }

  public Collection<Image> getImages() {
    return this.images;
  }

  public String getDescription() {
    return this.description;
  }

  public Long getCategoryId() {
    return this.category.getId();
  }

  public ZonedDateTime getCreation() {
    return this.creation;
  }

  public boolean isOwnedBy(User user) {
    return this.getUserId().equals(user.getId());
  }
}
