package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.*;
import br.com.zupacademy.sergio.ecommerce.model.dto.EncodedPassword;
import br.com.zupacademy.sergio.ecommerce.model.dto.QuestionRequest;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.QuestionRepository;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import br.com.zupacademy.sergio.ecommerce.security.AccountCredentials;
import br.com.zupacademy.sergio.ecommerce.security.JwtConfiguration;
import br.com.zupacademy.sergio.ecommerce.security.PasswordEncoding;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;

import static br.com.zupacademy.sergio.ecommerce.MailComposer.composedEmailMessageFromQuestion;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class QuestionControllerTests {
  private final MockMvc mockMvc;
  private final JwtConfiguration jwtConfiguration;
  private final QuestionRepository questionRepository;

  private final Gson gson = new Gson();

  private String tokenWithPrefix;
  private Long productId;


  @Autowired
  public QuestionControllerTests(
    MockMvc mockMvc,
    JwtConfiguration jwtConfiguration,
    QuestionRepository questionRepository
  ) {
    this.mockMvc = mockMvc;
    this.jwtConfiguration = jwtConfiguration;
    this.questionRepository = questionRepository;
  }

  @BeforeEach
  void setUp(
    @Autowired UserRepository userRepository,
    @Autowired ProductRepository productRepository,
    @Autowired CategoryRepository categoryRepository,
    @Autowired PasswordEncoding passwordEncoding
  ) throws Exception {

    this.questionRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();

    AccountCredentials accountCredentials = new AccountCredentials(
      "d@e.fr", "123456"
    );

    this.productId = productRepository.save(new Product(
      userRepository.save(new User(
        "seller@f.hu", new EncodedPassword("won't login")
      )),
      "name",
      BigDecimal.ONE,
      1,
      List.of(
        new Property("property a", "description"),
        new Property("property b", "description"),
        new Property("property c", "description")
      ),
      "description",
      categoryRepository.save(new Category("category"))
    )).getId();

    userRepository.save(new User(
      accountCredentials.getUsername(),
      new EncodedPassword(passwordEncoding.encode(
        accountCredentials.getPassword()
      ))
    ));

    this.tokenWithPrefix = this.mockMvc
      .perform(
        post("/auth")
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(accountCredentials))
      )
      .andReturn().getResponse().getHeader(jwtConfiguration.getHeader());
  }

  @Test
  @DisplayName("Should return ok and the question persisted when the request is valid")
  void shouldReturnOkAndTheQuestionPersistedWhenTheRequestIsValid(
  ) throws Exception {

    QuestionRequest questionRequest = new QuestionRequest("title");

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(byteArrayOutputStream));

    this.mockMvc
      .perform(
        post("/products/" + this.productId + "/questions")
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(questionRequest))
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().json(this.gson.toJson(questionRequest)))
    ;

    List<Question> questions = this.questionRepository.findAll();
    assertEquals(1, questions.size());
    assertEquals(
      composedEmailMessageFromQuestion(questions.get(0)),
      byteArrayOutputStream.toString()
    );
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = " ")
  @DisplayName("Should return bad request when the title is null, empty or blank")
  void shouldReturnBadRequestWhenTheTitleIsNullEmptyOrBlank(
    String title
  ) throws Exception {

    this.mockMvc
      .perform(
        post("/products/" + this.productId + "/questions")
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(new QuestionRequest(title)))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("title"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty())
    ;
  }

  @Test
  @DisplayName("Should return bad request when the product id does not exist")
  void shouldReturnBadRequestWhenTheProductIdDoesNotExist() throws Exception {

    this.mockMvc
      .perform(
        post("/products/999/questions")
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(new QuestionRequest("title")))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("productId"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty())
    ;
  }

  @Test
  @DisplayName("Should return forbidden when jwt is not sent")
  void shouldReturnForbiddenWhenJwtIsNotSent() throws Exception {

    this.mockMvc
      .perform(
        post("/products/" + this.productId + "/questions")
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(new QuestionRequest("title")))
      )
      .andExpect(status().isForbidden())
      .andExpect(content().string(""))
    ;
  }
}
