package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.Category;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.CategoryDto;
import br.com.zupacademy.sergio.ecommerce.model.dto.EncodedPassword;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import br.com.zupacademy.sergio.ecommerce.security.AccountCredentials;
import br.com.zupacademy.sergio.ecommerce.security.JwtConfiguration;
import br.com.zupacademy.sergio.ecommerce.security.PasswordEncoding;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTests {

  private final CategoryRepository categoryRepository;
  private final MockMvc mockMvc;
  private final JwtConfiguration jwtConfiguration;
  private final Gson gson = new Gson();

  private final String urlTemplate = "/categories";
  private String tokenWithPrefix;

  @Autowired
  public CategoryControllerTests(
    CategoryRepository categoryRepository,
    MockMvc mockMvc,
    JwtConfiguration jwtConfiguration
  ) {
    this.categoryRepository = categoryRepository;
    this.mockMvc = mockMvc;
    this.jwtConfiguration = jwtConfiguration;

  }

  @BeforeEach
  void setUp(
    @Autowired UserRepository userRepository,
    @Autowired PasswordEncoding passwordEncoding
  ) throws Exception {
    this.categoryRepository.deleteAll();

    AccountCredentials accountCredentials = new AccountCredentials(
      "a@be.co", "123456"
    );

    userRepository.deleteAll();
    userRepository.save(
      new User(
        accountCredentials.getUsername(),
        new EncodedPassword(
          passwordEncoding.encode(accountCredentials.getPassword())
        )
      )
    );

    this.tokenWithPrefix = this.mockMvc.perform(
      post("/auth").content(this.gson.toJson(accountCredentials))
    ).andReturn().getResponse().getHeader(this.jwtConfiguration.getHeader());
  }

  @Test
  @DisplayName("Should return bad request and validation errors when the object is empty")
  void shouldReturnBadRequestAndValidationErrorsWhenTheObjectIsEmpty() throws Exception {

    this.mockMvc
      .perform(
        MockMvcRequestBuilders
          .post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content("{ }")
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.*", Matchers.hasSize(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors", Matchers.hasSize(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].*", Matchers.hasSize(2)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].name").value("name"))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].message").isString())
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].message").isNotEmpty());

  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = " ")
  @DisplayName("Should return bad request when the request has a category with no name")
  void shouldReturnBadRequestWhenTheRequestHasACategoryWithNoName(String name) throws Exception {

    CategoryDto categoryDto = new CategoryDto(name, null);

    this.mockMvc
      .perform(
        MockMvcRequestBuilders
          .post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(categoryDto))
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.*", Matchers.hasSize(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors", Matchers.hasSize(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].*", Matchers.hasSize(2)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].name").value("name"))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].message").isString())
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].message").isNotEmpty());

  }

  @Test
  @DisplayName("Should return ok and the created category when the request has a valid name and no parent")
  void shouldReturnOkAndTheCreatedCategoryWhenTheRequestHasAValidNameAndNoParent() throws Exception {

    String name = "category 8jr7dh2";
    CategoryDto categoryDto = new CategoryDto(name, null);

    this.mockMvc
      .perform(
        MockMvcRequestBuilders
          .post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(categoryDto))
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.*", Matchers.hasSize(2)))
      .andExpect(MockMvcResultMatchers.jsonPath("name").value(name))
      .andExpect(MockMvcResultMatchers.jsonPath("parentId", Matchers.nullValue()));

    Assertions.assertTrue(this.categoryRepository.findByName(name).isPresent());
  }

  @Test
  @DisplayName("Should return ok and the created category when the request has a valid name and parent")
  void shouldReturnOkAndTheCreatedCategoryWhenTheRequestHasAValidNameAndParent() throws Exception {
    String name = "category 847jf7s2";
    Category parent = this.categoryRepository.save(new Category("parent category 948js8j"));
    CategoryDto categoryDto = new CategoryDto(name, parent.getId());

    this.mockMvc
      .perform(
        MockMvcRequestBuilders
          .post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(categoryDto))
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.*", Matchers.hasSize(2)))
      .andExpect(MockMvcResultMatchers.jsonPath("name").value(name))
      .andExpect(MockMvcResultMatchers.jsonPath("parentId").value(parent.getId()));

    Optional<Category> queriedCategory = this.categoryRepository.findByName(name);
    Assertions.assertTrue(queriedCategory.isPresent());

    Category category = queriedCategory.get();
    Assertions.assertEquals(parent.getId(), category.getParentId());
  }

  @Test
  @DisplayName("Should return bad request when the provided parent id does not exists")
  void shouldReturnBadRequestWhenTheProvidedParentIdDoesNotExists() throws Exception {
    String name = "category 47j49k22";
    CategoryDto categoryDto = new CategoryDto(name, 999L);

    this.mockMvc
      .perform(
        MockMvcRequestBuilders
          .post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(categoryDto))
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.*", Matchers.hasSize(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors", Matchers.hasSize(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].*", Matchers.hasSize(2)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].name").value("parentId"))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].message").isString())
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].message").isNotEmpty());

    Assertions.assertEquals(0, this.categoryRepository.countByName(name));
  }

  @Test
  @DisplayName("Should return bad request when the new category name already exists")
  void shouldReturnBadRequestWhenTheNewCategoryNameAlreadyExists() throws Exception {

    String name = "name of the category";
    this.categoryRepository.save(new Category(name));
    CategoryDto categoryDto = new CategoryDto(name, null);

    this.mockMvc
      .perform(
        MockMvcRequestBuilders
          .post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(categoryDto))
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.*", Matchers.hasSize(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors", Matchers.hasSize(1)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].*", Matchers.hasSize(2)))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].name").value("name"))
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].message").isString())
      .andExpect(MockMvcResultMatchers.jsonPath("fieldErrors[0].message").isNotEmpty());

    Assertions.assertEquals(1, this.categoryRepository.countByName(name));
  }

  @Test
  @DisplayName("Should return forbidden when JWT authorization token is not sent")
  void shouldReturnForbiddenWhenJwtAuthorizationTokenIsNotSent() throws Exception {
    String name = "category 234892ji";
    CategoryDto categoryDto = new CategoryDto(name, null);

    this.mockMvc
      .perform(
        MockMvcRequestBuilders
          .post(this.urlTemplate)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(categoryDto))
      )
      .andExpect(MockMvcResultMatchers.status().isForbidden());

    Assertions.assertEquals(0, this.categoryRepository.countByName(name));
  }

}
