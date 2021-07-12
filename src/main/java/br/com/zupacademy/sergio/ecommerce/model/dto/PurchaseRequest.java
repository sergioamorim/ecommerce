package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Gateway;
import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.Purchase;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.validation.ForeignKeyExists;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PurchaseRequest {

  @ForeignKeyExists(domainClass = Product.class, nullable = false)
  private final Long productId;

  @NotNull
  @Positive
  private final Integer quantity;

  @NotNull
  private final Gateway gateway;

  public PurchaseRequest(Long productId, Integer quantity, Gateway gateway) {
    this.productId = productId;
    this.quantity = quantity;
    this.gateway = gateway;
  }

  public Purchase toPurchase(Product product, User user) {
    return new Purchase(
      product,
      user,
      this.quantity,
      this.gateway
    );
  }

  public Long getProductId() {
    return this.productId;
  }

  public Integer getQuantity() {
    return this.quantity;
  }

  public Gateway getGateway() {
    return this.gateway;
  }
}
