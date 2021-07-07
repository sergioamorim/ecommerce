package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.ProductProperty;

import javax.validation.constraints.NotBlank;

public class ProductPropertyDto {

  @NotBlank
  private final String name;

  @NotBlank
  private final String description;

  public ProductPropertyDto(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public ProductPropertyDto(ProductProperty productProperty) {
    this.name = productProperty.getName();
    this.description = productProperty.getDescription();
  }

  public ProductProperty toProductProperty() {
    return new ProductProperty(this.name, this.description);
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductPropertyDto)) return false;
    if (this == o) return true;

    ProductPropertyDto that = (ProductPropertyDto) o;

    return this.getName().equals(that.getName());
  }

  @Override
  public int hashCode() {
    if (null != this.name) {
      return this.name.hashCode();
    }
    return 0;
  }
}
