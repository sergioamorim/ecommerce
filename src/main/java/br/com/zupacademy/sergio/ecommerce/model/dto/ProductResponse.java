package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Product;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class ProductResponse {

  private final Long id;
  private final Long userId;
  private final String name;
  private final BigDecimal price;
  private final Integer availableQuantity;
  private final Collection<PropertyDto> properties;
  private final String description;
  private final Long categoryId;
  private final ZonedDateTime creation;

  public ProductResponse(Product product) {

    this.id = product.getId();
    this.userId = product.getUserId();
    this.name = product.getName();
    this.price = product.getPrice();
    this.availableQuantity = product.getAvailableQuantity();
    this.properties = product.getProperties()
      .stream()
      .map(PropertyDto::new)
      .collect(Collectors.toSet());
    this.description = product.getDescription();
    this.categoryId = product.getCategoryId();
    this.creation = product.getCreation();

  }

  public Long getId() {
    return this.id;
  }

  public Long getUserId() {
    return this.userId;
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

  public Collection<PropertyDto> getProperties() {
    return this.properties;
  }

  public String getDescription() {
    return this.description;
  }

  public Long getCategoryId() {
    return this.categoryId;
  }

  public ZonedDateTime getCreation() {
    return this.creation;
  }
}
