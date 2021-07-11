package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Question;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDate;

public class QuestionResponse {

  private final String title;
  private final String userEmail;
  private final LocalDate creation;

  @JsonCreator
  public QuestionResponse(Question question) {
    this.title = question.getTitle();
    this.userEmail = question.getUserEmail();
    this.creation = question.getCreation().toLocalDate();
  }

  public String getTitle() {
    return this.title;
  }

  public String getUserEmail() {
    return this.userEmail;
  }

  public LocalDate getCreation() {
    return this.creation;
  }
}
