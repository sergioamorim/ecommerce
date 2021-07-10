package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Category;
import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.PropertyRepository;
import br.com.zupacademy.sergio.ecommerce.validation.ForeignKeyExists;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ProductRequest {

  @NotBlank
  private final String name;

  @NotNull
  @Positive
  private final BigDecimal price;

  @NotNull
  @PositiveOrZero
  private final Integer availableQuantity;

  @Size(min = 3)
  private final Collection<@Valid PropertyDto> properties;

  @NotBlank
  @Length(max = 1000)
  private final String description;

  @ForeignKeyExists(domainClass = Category.class, nullable = false)
  private final Long categoryId;

  public ProductRequest(
    String name,
    BigDecimal price,
    Integer availableQuantity,
    Collection<PropertyDto> properties,
    String description,
    Long categoryId
  ) {
    this.name = name;
    this.price = price;
    this.availableQuantity = availableQuantity;
    this.properties = properties;
    this.description = description;
    this.categoryId = categoryId;
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

  public Product toProduct(
    PropertyRepository propertyRepository,
    CategoryRepository categoryRepository,
    User user
  ) {
    return new Product(
      user,
      this.name,
      this.price,
      this.availableQuantity,
      this.properties.stream()
        .map(PropertyDto::toProperty)
        .collect(Collectors.toList()),
      this.description,
      categoryRepository.findById(this.categoryId).orElseThrow()  // ForeignKeyExists validation shall guarantee this exists
    );
  }

  public boolean hasPropertiesWithTheSameName() {
    return (
      new HashSet<>(this.properties).size()
        != this.properties.size()
    );
  }
}
