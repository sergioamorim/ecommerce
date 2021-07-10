package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.*;
import br.com.zupacademy.sergio.ecommerce.model.dto.EncodedPassword;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ImageRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import br.com.zupacademy.sergio.ecommerce.security.AccountCredentials;
import br.com.zupacademy.sergio.ecommerce.security.JwtConfiguration;
import br.com.zupacademy.sergio.ecommerce.security.PasswordEncoding;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTests {
  private final MockMvc mockMvc;
  private final JwtConfiguration jwtConfiguration;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final PasswordEncoding passwordEncoding;

  private final Gson gson = new Gson();

  private String tokenWithPrefix;
  private Long productId;

  @Autowired
  public ImageControllerTests(
    MockMvc mockMvc,
    JwtConfiguration jwtConfiguration,
    UserRepository userRepository,
    ProductRepository productRepository,
    PasswordEncoding passwordEncoding
  ) {
    this.mockMvc = mockMvc;
    this.jwtConfiguration = jwtConfiguration;
    this.userRepository = userRepository;
    this.productRepository = productRepository;
    this.passwordEncoding = passwordEncoding;
  }

  @BeforeEach
  void setUp(
    @Autowired CategoryRepository categoryRepository
  ) throws Exception {

    AccountCredentials accountCredentials = new AccountCredentials(
      "a@be.co", "123456"
    );

    this.productRepository.deleteAll();
    this.userRepository.deleteAll();
    categoryRepository.deleteAll();
    this.productId = this.productRepository.save(
      new Product(
        this.userRepository.save(new User(
          accountCredentials.getUsername(),
          new EncodedPassword(
            this.passwordEncoding.encode(accountCredentials.getPassword())
          )
        )),
        "product name",
        BigDecimal.ONE,
        1,
        List.of(
          new Property("product property a", "description"),
          new Property("product property b", "description"),
          new Property("product property c", "description")
        ),
        "d",
        categoryRepository.save(new Category("category name"))
      )
    ).getId();

    this.tokenWithPrefix = this.mockMvc
      .perform(
        post("/auth").content(this.gson.toJson(accountCredentials))
      )
      .andReturn().getResponse().getHeader(this.jwtConfiguration.getHeader());
  }

  @Test
  @DisplayName("Should return ok and the images URLs when the product is owned by the user")
  void shouldReturnOkAndTheImagesUrlsWhenTheProductIsOwnedByTheUser(
    @Autowired ImageRepository imageRepository
  ) throws Exception {

    imageRepository.deleteAll();

    MvcResult mvcResult = this.mockMvc
      .perform(
        multipart("/products/" + this.productId + "/images")
          .file(new MockMultipartFile(
            "multipartImages",
            getClass().getResourceAsStream("fake photo")
          ))
          .file(new MockMultipartFile(
            "multipartImages",
            getClass().getResourceAsStream("fake photo")
          ))
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("uploadedImages", hasSize(2)))
      .andExpect(jsonPath("uploadedImages[0].*", hasSize(1)))
      .andExpect(jsonPath(
        "uploadedImages[0].url",
        matchesPattern("https://fakecloud.com/file/.+")
      ))
      .andExpect(jsonPath("uploadedImages[1].*", hasSize(1)))
      .andExpect(jsonPath(
        "uploadedImages[1].url",
        matchesPattern("https://fakecloud.com/file/.+")
      ))
      .andReturn();

    String firstImageUrl = parse(mvcResult.getResponse().getContentAsString())
      .read("uploadedImages[0].url");

    String secondImageUrl = parse(mvcResult.getResponse().getContentAsString())
      .read("uploadedImages[1].url");

    assertNotEquals(firstImageUrl, secondImageUrl);

    Collection<Image> productImages = this.productRepository
      .findById(productId).orElseThrow().getImages();

    assertEquals(2, productImages.size());

    productImages.forEach(image -> assertTrue(
      image.getUrl().equals(firstImageUrl)
        ^ image.getUrl().equals(secondImageUrl)
    ));

  }

  @Test
  @DisplayName("Should return forbidden when the product is from another user")
  void shouldReturnForbiddenWhenTheProductIsFromAnotherUser() throws Exception {

    AccountCredentials accountCredentials = new AccountCredentials(
      "b@ce.de", "123456"
    );

    this.userRepository.save(new User(
      accountCredentials.getUsername(),
      new EncodedPassword(
        this.passwordEncoding.encode(accountCredentials.getPassword())
      )
    ));

    String newUserTokenWithPrefix = this.mockMvc
      .perform(
        post("/auth").content(this.gson.toJson(accountCredentials))
      )
      .andReturn().getResponse().getHeader(this.jwtConfiguration.getHeader());

    this.mockMvc
      .perform(
        multipart("/products/" + this.productId + "/images")
          .file(new MockMultipartFile(
            "multipartImages",
            getClass().getResourceAsStream("fake photo")
          ))
          .header(this.jwtConfiguration.getHeader(), newUserTokenWithPrefix)
      )
      .andExpect(status().isForbidden())
      .andExpect(content().string(""))
    ;
  }

  @Test
  @DisplayName("Should return bad request when the product id does not exist")
  void shouldReturnBadRequestWhenTheProductIdDoesNotExist() throws Exception {
    this.mockMvc
      .perform(
        multipart("/products/999/images")
          .file(new MockMultipartFile(
            "multipartImages",
            getClass().getResourceAsStream("fake photo")
          ))
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("productId"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty())
    ;
  }

  @Test
  @DisplayName("Should return forbidden when the JWT is not sent")
  void shouldReturnForbiddenWhenTheJwtIsNotSent() throws Exception {
    this.mockMvc
      .perform(
        multipart("/products/" + this.productId + "/images")
          .file(new MockMultipartFile(
            "multipartImages",
            getClass().getResourceAsStream("fake photo")
          ))
      )
      .andExpect(status().isForbidden())
      .andExpect(content().string(""))
    ;
  }

}
