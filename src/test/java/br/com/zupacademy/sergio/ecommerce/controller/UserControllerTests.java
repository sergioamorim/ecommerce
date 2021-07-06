package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.UserRequest;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

  private final MockMvc mockMvc;
  private final UserRepository userRepository;

  private final String urlTemplate = "/users";

  private final Gson gson = new Gson();

  @Autowired
  public UserControllerTests(
    MockMvc mockMvc,
    UserRepository userRepository
  ) {
    this.mockMvc = mockMvc;
    this.userRepository = userRepository;
  }

  @BeforeEach
  void setUp() {
    this.userRepository.deleteAll();
  }

  @Test
  @DisplayName("Should return bad request when the body is empty")
  void shouldReturnBadRequestWhenTheBodyIsEmpty() throws Exception {
    this.mockMvc
      .perform(post(this.urlTemplate))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when the object is empty")
  void shouldReturnBadRequestWhenTheObjectIsEmpty() throws Exception {
    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .contentType(MediaType.APPLICATION_JSON)
          .content("{ }")
      )
      .andExpect(status().isBadRequest());

  }

  @ParameterizedTest
  @NullAndEmptySource  // email should not be null and should not be empty
  @ValueSource(strings = {
    " ",  // email should not be blank
    "a",  // email should have a valid email format
  })
  @DisplayName("Should not allow an user to be created with an invalid email")
  void shouldNotAllowAnUserToBeCreatedWithAnInvalidEmail(
    String email
  ) throws Exception {
    UserRequest userRequest = new UserRequest(email, "password");
    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(userRequest))
      )
      .andExpect(status().isBadRequest());
    assertTrue(this.userRepository.findByEmail(email).isEmpty());
  }

  @ParameterizedTest
  @NullAndEmptySource  // password should not be null and should not be empty
  @ValueSource(strings = {
    "   \n \n \t",  // password should not be blank
    "12345",  // password should have at least six characters
  })
  @DisplayName(
    "Should not allow an user to be created with an invalid password"
  )
  void shouldNotAllowAnUserToBeCreatedWithAnInvalidPassword(
    String password
  ) throws Exception {
    String email = "email@example.com";
    UserRequest userRequest = new UserRequest(email, password);

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(userRequest))
      )
      .andExpect(status().isBadRequest());
    assertTrue(this.userRepository.findByEmail(email).isEmpty());
  }

  @Test
  @DisplayName("Should create a user when the request is valid")
  void shouldCreateAUserWhenTheRequestIsValid() throws Exception {
    String email = "email@example.com";
    String password = "123456";
    UserRequest userRequest = new UserRequest(email, password);

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(userRequest))
      )
      .andExpect(status().isOk());

    Optional<User> optionalUser = this.userRepository.findByEmail(email);
    assertTrue(optionalUser.isPresent());

    User user = optionalUser.get();
    assertTrue(BCrypt.checkpw(password, user.getPassword()));

    assertTrue(user.getCreation().isBefore(ZonedDateTime.now()));
  }

  @Test
  @DisplayName("Should not create a user with duplicate email")
  void shouldNotCreateAUserWithDuplicateEmail() throws Exception {

    String email = "duplicate@email.com";
    UserRequest userRequestA = new UserRequest(email, "123456");

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(userRequestA))
      )
    ;

    UserRequest userRequestB = new UserRequest(email, "654321");

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(userRequestB))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("email"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());

    assertEquals(1, this.userRepository.countByEmail(email));
  }
}
