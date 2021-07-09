package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.Question;
import br.com.zupacademy.sergio.ecommerce.model.User;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;

public class QuestionDto {

  @NotBlank
  private final String title;

  @JsonCreator
  public QuestionDto(Question question) {
    this.title = question.getTitle();
  }

  public QuestionDto(String title) {
    this.title = title;
  }

  public Question toQuestion(Product product, User user) {
    return new Question(this.title, product, user);
  }

  public String getTitle() {
    return this.title;
  }
}
