package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.Question;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.QuestionRequest;
import br.com.zupacademy.sergio.ecommerce.model.dto.QuestionResponse;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.QuestionRepository;
import br.com.zupacademy.sergio.ecommerce.validation.ForeignKeyExists;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

import static br.com.zupacademy.sergio.ecommerce.MailSender.questionOfEmailSent;

@Validated
@RestController
public class QuestionController {
  private final QuestionRepository questionRepository;
  private final ProductRepository productRepository;

  @Autowired
  public QuestionController(
    QuestionRepository questionRepository, ProductRepository productRepository
  ) {
    this.questionRepository = questionRepository;
    this.productRepository = productRepository;
  }

  @PostMapping("/products/{productId}/questions")
  public ResponseEntity<QuestionResponse> createQuestion(
    @PathVariable @ForeignKeyExists(domainClass = Product.class) Long productId,
    @RequestBody @Valid QuestionRequest questionRequest,
    @AuthenticationPrincipal User user
  ) throws TemplateException, IOException {
    return ResponseEntity.ok(new QuestionResponse(questionOfEmailSent(
      this.persistedQuestion(questionRequest, productId, user)
    )));

  }

  private Question persistedQuestion(
    QuestionRequest questionRequest, Long productId, User user
  ) {

    return this.questionRepository.save(questionRequest.toQuestion(
      this.productRepository.findById(productId).orElseThrow(), user
    ));

  }
}
