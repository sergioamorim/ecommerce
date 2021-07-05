package br.com.zupacademy.sergio.ecommerce.model;

import javax.persistence.*;

@Entity
public class Category {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToOne
  private Category parent;

  public Category(String name) {
    this.name = name;
  }

  public Category(String name, Category parent) {
    this.name = name;
    this.parent = parent;
  }

  @Deprecated  // jpa
  protected Category() {
  }

  public Long getParentId() {
    if (null != this.parent) {
      return this.parent.getId();
    }
    return null;
  }


  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }
}
