package br.com.zupacademy.sergio.ecommerce;

import br.com.zupacademy.sergio.ecommerce.model.Question;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;

public class MailComposer {

  public static String composedEmailMessageFromQuestion(
    Question question
  ) throws TemplateException, IOException {

    return emailMessageTemplateFilledWithParameters(
      emailParametersFromQuestion(question)
    );

  }

  private static HashMap<String, String> emailParametersFromQuestion(
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
    HashMap<String, String> emailParameters
  ) throws IOException, TemplateException {

    return FreeMarkerTemplateUtils.processTemplateIntoString(
      new Configuration(
        new Version("2.3.0")
      ).getTemplate("src/main/resources/templates/question-made.ftl"),
      emailParameters
    );

  }
}
