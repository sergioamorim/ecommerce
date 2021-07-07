package br.com.zupacademy.sergio.ecommerce.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ProductProperty {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  public ProductProperty(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Deprecated  // jpa
  protected ProductProperty() {
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}
