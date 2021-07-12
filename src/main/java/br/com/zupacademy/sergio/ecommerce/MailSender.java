package br.com.zupacademy.sergio.ecommerce;

import br.com.zupacademy.sergio.ecommerce.model.Purchase;
import br.com.zupacademy.sergio.ecommerce.model.Question;
import freemarker.template.TemplateException;

import java.io.IOException;

import static br.com.zupacademy.sergio.ecommerce.MailComposer.composedEmailMessageFromPurchase;
import static br.com.zupacademy.sergio.ecommerce.MailComposer.composedEmailMessageFromQuestion;

public class MailSender {

  public static Purchase purchaseOfEmailSent(
    Purchase purchase
  ) throws TemplateException, IOException {

    System.out.println(composedEmailMessageFromPurchase(purchase));
    return purchase;

  }

  public static Question questionOfEmailSent(
    Question question
  ) throws TemplateException, IOException {

    System.out.print(composedEmailMessageFromQuestion(question));
    return question;

  }
}
