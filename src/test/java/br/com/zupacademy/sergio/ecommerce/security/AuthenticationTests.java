package br.com.zupacademy.sergio.ecommerce.security;

import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.EncodedPassword;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTests {
  private final UserRepository userRepository;
  private final PasswordEncoding passwordEncoding;
  private final JwtConfiguration jwtConfiguration;
  private final MockMvc mockMvc;

  private final String urlTemplate = "/auth";

  private final Gson gson = new Gson();
  private final AccountCredentials validAccountCredentials = new AccountCredentials(
    "username.email@example.com", "password.123456"
  );

  @Autowired
  public AuthenticationTests(
    UserRepository userRepository,
    PasswordEncoding passwordEncoding,
    JwtConfiguration jwtConfiguration,
    MockMvc mockMvc
  ) {
    this.userRepository = userRepository;
    this.passwordEncoding = passwordEncoding;
    this.jwtConfiguration = jwtConfiguration;
    this.mockMvc = mockMvc;

  }

  @BeforeEach
  void setUp() {
    this.userRepository.deleteAll();
    this.userRepository.save(
      new User(
        this.validAccountCredentials.getUsername(),
        new EncodedPassword(
          passwordEncoding.encode(this.validAccountCredentials.getPassword())
        )
      )
    );
  }

  @Test
  @DisplayName("Should authenticate a user when the credentials are valid")
  void shouldAuthenticateAUserWhenTheCredentialsAreValid() throws Exception {

    MvcResult mvcResult = this.mockMvc.perform(
      post(this.urlTemplate).content(this.gson.toJson(this.validAccountCredentials))
    )
      .andExpect(status().isOk())
      .andExpect(content().string(""))
      .andExpect(header().exists(this.jwtConfiguration.getHeader())).andReturn();

    assertTrue(
      Objects.requireNonNull(
        mvcResult
          .getResponse()
          .getHeader(this.jwtConfiguration.getHeader())
      ).startsWith(this.jwtConfiguration.getTokenPrefix() + " ")
    );
  }

  @Test
  @DisplayName("Should return 404 on undefined endpoint when the JWT token is valid")
  void shouldReturn404OnUndefinedEndpointWhenTheJwtTokenIsValid() throws Exception {
    String tokenWithPrefix = this.mockMvc.perform(
      post(this.urlTemplate).content(this.gson.toJson(this.validAccountCredentials))
    ).andReturn().getResponse().getHeader(this.jwtConfiguration.getHeader());

    this.mockMvc
      .perform(
        get("/undefined-endpoint-87rj82js").header(
          this.jwtConfiguration.getHeader(), tokenWithPrefix
        )
      )
      .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return unauthorized when the credentials are invalid")
  void shouldReturnUnauthorizedWhenTheCredentialsAreInvalid() throws Exception {
    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .content(
            this.gson.toJson(
              new AccountCredentials(
                "usernotregistered193771@gmail.com",
                "password from user not registered"
              )
            )
          )
      )
      .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return forbidden when the JWT is invalid")
  void shouldReturnForbiddenWhenTheJwtIsInvalid() throws Exception {
    String tokenWithPrefix = this.mockMvc.perform(
      post(this.urlTemplate).content(this.gson.toJson(this.validAccountCredentials))
    ).andReturn().getResponse().getHeader(this.jwtConfiguration.getHeader());


    assert tokenWithPrefix != null;
    this.mockMvc
      .perform(
        get("/undefined-endpoint-87rj82js").header(
          this.jwtConfiguration.getHeader(),
          tokenWithPrefix.replace("a", "b")
        )
      )
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when the JWT is expired")
  void shouldReturnForbiddenWhenTheJwtIsExpired(
    @Autowired JwtAuthenticationService jwtAuthenticationService
  ) throws Exception {

    String expiredTokenWithPrefix =
      this.jwtConfiguration.getTokenPrefix()
        + " "
        + JWT.create()
        .withSubject(this.validAccountCredentials.getUsername())
        .withExpiresAt(Date.valueOf(LocalDate.now().minusDays(1)))
        .sign(jwtAuthenticationService.generatedSignAlgorithm());

    this.mockMvc
      .perform(
        get("/undefined-endpoint-87rj82js").header(
          this.jwtConfiguration.getHeader(), expiredTokenWithPrefix
        )
      )
      .andExpect(status().isForbidden());

  }
}
