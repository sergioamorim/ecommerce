package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.Category;
import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.ProductProperty;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.EncodedPassword;
import br.com.zupacademy.sergio.ecommerce.model.dto.ReviewRequest;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ReviewRepository;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import br.com.zupacademy.sergio.ecommerce.security.AccountCredentials;
import br.com.zupacademy.sergio.ecommerce.security.JwtConfiguration;
import br.com.zupacademy.sergio.ecommerce.security.PasswordEncoding;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTests {
  private final MockMvc mockMvc;
  private final JwtConfiguration jwtConfiguration;
  private final Gson gson = new Gson();

  private String tokenWithPrefix;
  private Long productId;

  @Autowired
  public ReviewControllerTests(
    MockMvc mockMvc, JwtConfiguration jwtConfiguration
  ) {
    this.mockMvc = mockMvc;
    this.jwtConfiguration = jwtConfiguration;
  }

  static Stream<String> stringNullEmptyBlankAndMoreThan500Characters() {
    return Stream.of(null, "", " ", "a".repeat(501));
  }

  @BeforeEach
  void setUp(
    @Autowired PasswordEncoding passwordEncoding,
    @Autowired ProductRepository productRepository,
    @Autowired CategoryRepository categoryRepository,
    @Autowired UserRepository userRepository,
    @Autowired ReviewRepository reviewRepository
  ) throws Exception {

    reviewRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();

    this.productId = productRepository.save(
      new Product(
        userRepository.save(new User(
          "reviwer@em.au", new EncodedPassword("won't login")
        )),
        "product name",
        BigDecimal.ONE,
        1,
        Set.of(
          new ProductProperty("product property a", "description"),
          new ProductProperty("product property b", "description"),
          new ProductProperty("product property c", "description")
        ),
        "d",
        categoryRepository.save(new Category("category name"))
      )
    ).getId();

    AccountCredentials accountCredentials = new AccountCredentials(
      "c@de.fr", "123456"
    );

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
      .andReturn().getResponse()
      .getHeader(jwtConfiguration.getHeader())
    ;
  }

  @Test
  @DisplayName("Should return ok when the product review is valid")
  void shouldReturnOkWhenTheProductReviewIsValid(
    @Autowired ReviewRepository reviewRepository
  ) throws Exception {

    ReviewRequest reviewRequest = new ReviewRequest(
      1, "title", "description"
    );

    this.mockMvc
      .perform(
        post("/products/" + this.productId + "/reviews")
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(reviewRequest))
      )
      .andExpect(status().isOk())
      .andExpect(content().string(""))
    ;

    assertEquals(1, reviewRepository.findAll().size());
  }

  @Test
  @DisplayName("Should return forbidden when the token is not sent")
  void shouldReturnForbiddenWhenTheTokenIsNotSent() throws Exception {
    ReviewRequest reviewRequest = new ReviewRequest(
      1, "title", "description"
    );

    this.mockMvc
      .perform(
        post("/products/" + this.productId + "/reviews")
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(reviewRequest))
      )
      .andExpect(status().isForbidden())
      .andExpect(content().string(""))
    ;
  }

  @Test
  @DisplayName("Should return bad request when the product id is invalid")
  void shouldReturnBadRequestWhenTheProductIdIsInvalid() throws Exception {
    ReviewRequest reviewRequest = new ReviewRequest(
      1, "title", "description"
    );

    this.mockMvc
      .perform(
        post("/products/999/reviews")
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(reviewRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(
        jsonPath("fieldErrors[0].name").value("productId")
      )
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty())
    ;
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(ints = {0, 6})
  @DisplayName("Should return bad request when the rating is null, below one or above 5")
  void shouldReturnBadRequestWhenTheRatingIsNullBelowOneOrAbove5(
    Integer rating
  ) throws Exception {
    ReviewRequest reviewRequest = new ReviewRequest(
      rating, "title", "description"
    );

    this.mockMvc
      .perform(
        post("/products/" + this.productId + "/reviews")
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(reviewRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(
        jsonPath("fieldErrors[0].name").value("rating")
      )
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty())
    ;
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  @DisplayName("Should return bad request when the title is null, empty or blank")
  void shouldReturnBadRequestWhenTheTitleIsNullEmptyOrBlank(
    String title
  ) throws Exception {

    ReviewRequest reviewRequest = new ReviewRequest(
      1, title, "description"
    );

    this.mockMvc
      .perform(
        post("/products/" + this.productId + "/reviews")
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(reviewRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(
        jsonPath("fieldErrors[0].name").value("title")
      )
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty())
    ;
  }

  @ParameterizedTest
  @MethodSource("stringNullEmptyBlankAndMoreThan500Characters")
  @DisplayName("Should return bad request when the description is null, empty, blank or has more then 500 characters")
  void shouldReturnBadRequestWhenTheDescriptionIsNullEmptyBlankOrHasMoreThen500Characters(
    String description
  ) throws Exception {

    ReviewRequest reviewRequest = new ReviewRequest(
      1, "title", description
    );

    this.mockMvc
      .perform(
        post("/products/" + this.productId + "/reviews")
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(reviewRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(
        jsonPath("fieldErrors[0].name").value("description")
      )
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty())
    ;
  }
}
