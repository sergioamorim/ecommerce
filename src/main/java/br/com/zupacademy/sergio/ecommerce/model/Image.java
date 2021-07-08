package br.com.zupacademy.sergio.ecommerce.model;

import javax.persistence.*;

@Entity
public class Image {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(optional = false)
  private Product product;

  @Column(nullable = false)
  private String url;

  public Image(Product product, String url) {
    this.product = product;
    this.url = url;
  }

  @Deprecated
  protected Image() {
  }

  public String getUrl() {
    return this.url;
  }
}
