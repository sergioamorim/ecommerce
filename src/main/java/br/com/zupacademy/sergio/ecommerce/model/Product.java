package br.com.zupacademy.sergio.ecommerce.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
  private Collection<Property> properties;

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
    Collection<Property> properties,
    String description,
    Category category
  ) {
    this.user = user;
    this.name = name;
    this.price = price;
    this.availableQuantity = availableQuantity;
    this.properties = properties;
    this.description = description;
    this.category = category;
  }

  @Deprecated  // jpa
  protected Product() {
  }

  public boolean removeQuantity(Integer quantityToRemove) {
    if (this.availableQuantity >= quantityToRemove) {
      this.availableQuantity -= quantityToRemove;
      return true;
    }
    return false;
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

  public <T> Collection<T> mapProperties(Function<Property, T> mapper) {
    return this.properties.stream().map(mapper).collect(Collectors.toList());
  }

  public <T> Collection<T> mapImages(Function<Image, T> mapper) {
    return this.images.stream().map(mapper).collect(Collectors.toList());
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
