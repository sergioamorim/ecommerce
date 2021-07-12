package br.com.zupacademy.sergio.ecommerce.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

import static br.com.zupacademy.sergio.ecommerce.model.Status.BEGIN;
import static javax.persistence.EnumType.STRING;

@Entity
public class Purchase {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(optional = false)
  private Product product;

  @ManyToOne(optional = false)
  private User user;

  @Column(nullable = false)
  private Integer quantity;

  @Enumerated(STRING)
  @Column(nullable = false)
  private Gateway gateway;

  @Enumerated(STRING)
  @Column(nullable = false)
  private Status status;

  @Column(nullable = false)
  private UUID uuid;

  @Column(nullable = false)
  private BigDecimal productPrice;

  public Purchase(Product product, User user, Integer quantity, Gateway gateway) {
    this.product = product;
    this.user = user;
    this.quantity = quantity;
    this.gateway = gateway;
    this.status = BEGIN;
    this.uuid = UUID.randomUUID();
    this.productPrice = this.product.getPrice();
  }

  @Deprecated  // jpa
  protected Purchase() {
  }

  public String getRedirectUrl() {
    switch (this.gateway) {
      case PayPal:
        return "paypal.com?buyerId="
          + this.uuid + "&redirectUrl=urlRetornoAppPosPagamento";
      case PagSeguro:
        return "pagseguro.com?returnId="
          + this.uuid + "&redirectUrl=urlRetornoAppPosPagamento";
      default:
        return null;
    }
  }

  public String getProductUserEmail() {
    return this.product.getUserEmail();
  }

  public String getUserEmail() {
    return this.user.getEmail();
  }

  public String getProductName() {
    return this.product.getName();
  }

  public Integer getQuantity() {
    return this.quantity;
  }

  public BigDecimal getProductPrice() {
    return this.productPrice;
  }
}
