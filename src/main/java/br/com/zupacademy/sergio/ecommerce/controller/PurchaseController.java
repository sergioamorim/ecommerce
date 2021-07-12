package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.PurchaseRequest;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.PurchaseRepository;
import br.com.zupacademy.sergio.ecommerce.validation.ProductUnavailableQuantityValidator;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

import static br.com.zupacademy.sergio.ecommerce.MailSender.purchaseOfEmailSent;

@RestController
public class PurchaseController {
  private final PurchaseRepository purchaseRepository;
  private final ProductRepository productRepository;
  private final ProductUnavailableQuantityValidator productUnavailableQuantityValidator;

  @Autowired
  public PurchaseController(
    PurchaseRepository purchaseRepository,
    ProductRepository productRepository,
    ProductUnavailableQuantityValidator productUnavailableQuantityValidator
  ) {
    this.purchaseRepository = purchaseRepository;
    this.productRepository = productRepository;
    this.productUnavailableQuantityValidator = productUnavailableQuantityValidator;
  }

  @InitBinder
  private void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(this.productUnavailableQuantityValidator);
  }

  @PostMapping("/purchases")
  public ResponseEntity<?> createPurchase(
    @RequestBody @Valid PurchaseRequest purchaseRequest,
    @AuthenticationPrincipal User user
  ) throws TemplateException, IOException {

    return this.redirectToPaymentOrUnprocessableEntity(
      this.productRepository.findById(
        purchaseRequest.getProductId()
      ).orElseThrow(),  // purchaseRequest shall be validated with ForeignKeyExists on productId
      purchaseRequest,
      user
    );

  }

  private ResponseEntity<?> redirectToPaymentOrUnprocessableEntity(
    Product product, PurchaseRequest purchaseRequest, User user
  ) throws TemplateException, IOException {

    if (product.removeQuantity(purchaseRequest.getQuantity())) {

      return ResponseEntity.status(302)
        .header(
          "Location",
          purchaseOfEmailSent(this.purchaseRepository.save(
            purchaseRequest.toPurchase(product, user)
          )).getRedirectUrl()
        )
        .build();

    }

    return ResponseEntity.unprocessableEntity().build();
  }
}
