package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Property;

import javax.validation.constraints.NotBlank;

public class PropertyDto {

  @NotBlank
  private final String name;

  @NotBlank
  private final String description;

  public PropertyDto(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public PropertyDto(Property property) {
    this.name = property.getName();
    this.description = property.getDescription();
  }

  public Property toProperty() {
    return new Property(this.name, this.description);
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PropertyDto)) return false;
    if (this == o) return true;

    PropertyDto that = (PropertyDto) o;

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
