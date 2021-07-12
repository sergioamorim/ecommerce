package br.com.zupacademy.sergio.ecommerce;

import br.com.zupacademy.sergio.ecommerce.model.Purchase;
import br.com.zupacademy.sergio.ecommerce.model.Question;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;

public class MailComposer {

  public static String composedEmailMessageFromPurchase(
    Purchase purchase
  ) throws TemplateException, IOException {

    return emailMessageTemplateFilledWithParameters(
      purchaseEmailParametersFromPurchase(purchase), "purchase-made.ftl"
    );

  }

  public static String composedEmailMessageFromQuestion(
    Question question
  ) throws TemplateException, IOException {

    return emailMessageTemplateFilledWithParameters(
      questionEmailParametersFromQuestion(question), "question-made.ftl"
    );

  }

  private static HashMap<String, String> purchaseEmailParametersFromPurchase(
    Purchase purchase
  ) {
    HashMap<String, String> emailParameters = new HashMap<>();
    emailParameters.put("seller", purchase.getProductUserEmail());
    emailParameters.put("quantity", String.valueOf(purchase.getQuantity()));
    emailParameters.put("product", purchase.getProductName());
    emailParameters.put("buyer", purchase.getUserEmail());
    emailParameters.put("price", String.valueOf(purchase.getProductPrice()));
    return emailParameters;
  }

  private static HashMap<String, String> questionEmailParametersFromQuestion(
    Question question
  ) {
    HashMap<String, String> emailParameters = new HashMap<>();
    emailParameters.put("questioned", question.getProductUserEmail());
    emailParameters.put("questioner", question.getUserEmail());
    emailParameters.put("product", question.getProductName());
    emailParameters.put("question", question.getTitle());
    return emailParameters;
  }

  private static String emailMessageTemplateFilledWithParameters(
    HashMap<String, String> emailParameters, String template
  ) throws IOException, TemplateException {

    return FreeMarkerTemplateUtils.processTemplateIntoString(
      new Configuration(
        new Version("2.3.0")
      ).getTemplate("src/main/resources/templates/" + template),
      emailParameters
    );

  }
}
