package br.com.zupacademy.sergio.ecommerce;

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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

  private final Gson gson;
  private final MockMvc mockMvc;
  private final UserRepository userRepository;
  private final String urlTemplate = "/users";

  @Autowired
  public UserControllerTests(
    Gson gson,
    MockMvc mockMvc,
    UserRepository userRepository
  ) {
    this.gson = gson;
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
      .andExpect(status().isBadRequest())
      .andExpect(content().string(""));
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
      .andExpect(status().isBadRequest())
      .andExpect(content().string(""));
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
      .andExpect(status().isBadRequest())
      .andExpect(content().string(""));
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
          .contentType("application/json")
          .content(this.gson.toJson(userRequest))
      )
      .andExpect(status().isOk());

    Optional<User> optionalUser = this.userRepository.findByEmail(email);
    assertTrue(optionalUser.isPresent());

    User user = optionalUser.get();
    assertTrue(BCrypt.checkpw(password, user.getPassword()));

    assertTrue(user.getCreation().isBefore(ZonedDateTime.now()));
  }
}
