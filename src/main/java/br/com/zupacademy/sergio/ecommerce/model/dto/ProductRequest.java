package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Category;
import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductPropertyRepository;
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
  private final Collection<@Valid ProductPropertyDto> productProperties;

  @NotBlank
  @Length(max = 1000)
  private final String description;

  @ForeignKeyExists(domainClass = Category.class, nullable = false)
  private final Long categoryId;

  public ProductRequest(
    String name,
    BigDecimal price,
    Integer availableQuantity,
    Collection<ProductPropertyDto> productProperties,
    String description,
    Long categoryId
  ) {
    this.name = name;
    this.price = price;
    this.availableQuantity = availableQuantity;
    this.productProperties = productProperties;
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

  public Collection<ProductPropertyDto> getProductProperties() {
    return this.productProperties;
  }

  public String getDescription() {
    return this.description;
  }

  public Long getCategoryId() {
    return this.categoryId;
  }

  public Product toProduct(
    ProductPropertyRepository productPropertyRepository,
    CategoryRepository categoryRepository,
    User user
  ) {
    return new Product(
      user,
      this.name,
      this.price,
      this.availableQuantity,
      this.productProperties.stream()
        .map(
          productPropertyDto -> productPropertyRepository.save(
            productPropertyDto.toProductProperty()
          )
        )
        .collect(Collectors.toSet()),
      this.description,
      categoryRepository.findById(this.categoryId).get()
    );
  }

  public boolean hasPropertiesWithTheSameName() {
    return (
      new HashSet<>(this.productProperties).size()
        != this.productProperties.size()
    );
  }
}
