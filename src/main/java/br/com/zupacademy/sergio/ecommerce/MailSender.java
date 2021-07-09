package br.com.zupacademy.sergio.ecommerce;

import br.com.zupacademy.sergio.ecommerce.model.Question;
import freemarker.template.TemplateException;

import java.io.IOException;

import static br.com.zupacademy.sergio.ecommerce.MailComposer.composedEmailMessageFromQuestion;

public class MailSender {

  public static Question questionOfEmailSent(
    Question question
  ) throws TemplateException, IOException {

    System.out.print(composedEmailMessageFromQuestion(question));
    return question;

  }
}
