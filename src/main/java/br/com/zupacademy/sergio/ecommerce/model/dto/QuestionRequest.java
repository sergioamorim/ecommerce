package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.Question;
import br.com.zupacademy.sergio.ecommerce.model.User;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

public class QuestionRequest {

  @NotBlank
  private final String title;

  @JsonCreator(mode = PROPERTIES)
  public QuestionRequest(String title) {
    this.title = title;
  }

  public Question toQuestion(Product product, User user) {
    return new Question(this.title, product, user);
  }
}
