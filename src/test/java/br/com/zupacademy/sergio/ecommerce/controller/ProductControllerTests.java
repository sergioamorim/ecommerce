package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.Category;
import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.EncodedPassword;
import br.com.zupacademy.sergio.ecommerce.model.dto.ProductRequest;
import br.com.zupacademy.sergio.ecommerce.model.dto.PropertyDto;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import br.com.zupacademy.sergio.ecommerce.security.AccountCredentials;
import br.com.zupacademy.sergio.ecommerce.security.JwtConfiguration;
import br.com.zupacademy.sergio.ecommerce.security.PasswordEncoding;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {

  private final ProductRepository productRepository;
  private final MockMvc mockMvc;
  private final JwtConfiguration jwtConfiguration;
  private final Gson gson = new Gson();

  private final String urlTemplate = "/products";
  private String tokenWithPrefix;
  private Long userId;

  private ProductRequestBuilder productRequestBuilder;

  @Autowired
  public ProductControllerTests(
    ProductRepository productRepository,
    MockMvc mockMvc,
    JwtConfiguration jwtConfiguration
  ) {
    this.productRepository = productRepository;
    this.mockMvc = mockMvc;
    this.jwtConfiguration = jwtConfiguration;
  }

  @BeforeEach
  void setUp(
    @Autowired UserRepository userRepository,
    @Autowired CategoryRepository categoryRepository,
    @Autowired PasswordEncoding passwordEncoding
  ) throws Exception {
    this.productRepository.deleteAll();
    categoryRepository.deleteAll();
    this.productRequestBuilder = new ProductRequestBuilder(
      categoryRepository.save(new Category("category name ew8fah09"))
        .getId()
    );


    AccountCredentials accountCredentials = new AccountCredentials(
      "a@be.co", "123456"
    );

    userRepository.deleteAll();
    this.userId = userRepository.save(new User(
      accountCredentials.getUsername(),
      new EncodedPassword(
        passwordEncoding.encode(accountCredentials.getPassword())
      ))
    ).getId();

    this.tokenWithPrefix = this.mockMvc.perform(
      post("/auth").content(this.gson.toJson(accountCredentials))
    ).andReturn().getResponse().getHeader(this.jwtConfiguration.getHeader());

  }

  @Test
  @DisplayName("Should create a product with properties when the request is valid")
  void shouldCreateAProductWithPropertiesWhenTheRequestIsValid(
  ) throws Exception {

    ProductRequest productRequest = this.productRequestBuilder.build();

    MvcResult mvcResult = this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("id").isNotEmpty())
      .andExpect(jsonPath("userId").isNotEmpty())
      .andExpect(jsonPath("name").value(productRequest.getName()))
      .andExpect(jsonPath("price").value(productRequest.getPrice()))
      .andExpect(
        jsonPath("availableQuantity")
          .value(productRequest.getAvailableQuantity())
      )
      .andExpect(jsonPath(
        "properties",
        hasToString(this.gson.toJson(productRequest.getProperties()))
      ))
      .andExpect(
        jsonPath("description").value(productRequest.getDescription())
      )
      .andExpect(jsonPath("categoryId").value(productRequest.getCategoryId()))
      .andExpect(jsonPath("creation").isString())
      .andExpect(jsonPath("creation").isNotEmpty())
      .andReturn();

    Long returnedId = Long.valueOf(
      JsonPath.parse(mvcResult.getResponse().getContentAsString()).read("id").toString()
    );

    Optional<Product> optionalProduct = this.productRepository
      .findById(returnedId);

    assertTrue(optionalProduct.isPresent());
    Product product = optionalProduct.get();
    assertEquals(this.userId, product.getUserId());
    assertEquals(productRequest.getName(), product.getName());

    assertEquals(
      Double.valueOf(productRequest.getPrice().toString()),
      Double.valueOf(product.getPrice().toString())
    );

    assertEquals(
      productRequest.getAvailableQuantity(), product.getAvailableQuantity()
    );

    assertEquals(
      productRequest.getProperties().size(),
      product.mapProperties(PropertyDto::new).size()
    );

    assertEquals(productRequest.getDescription(), product.getDescription());
    assertEquals(productRequest.getCategoryId(), product.getCategoryId());
    assertTrue(product.getCreation().isBefore(ZonedDateTime.now()));
  }

  @Test
  @DisplayName("Should return bad request when the product has less then three properties")
  void shouldReturnBadRequestWhenTheProductHasLessThenThreeProperties(
  ) throws Exception {

    ProductRequest productRequest = this.productRequestBuilder
      .withQuantityOfProperties(2).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("properties"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());

  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  @DisplayName("Should return bad request when the name is null, empty or blank")
  void shouldReturnBadRequestWhenTheNameIsNullEmptyOrBlank(
    String name
  ) throws Exception {

    ProductRequest productRequest = this.productRequestBuilder
      .withName(name).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("name"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(doubles = {-1, -0.1, 0})
  @DisplayName("Should return bad request when price is null, negative or zero")
  void shouldReturnBadRequestWhenPriceIsNullNegativeOrZero(
    Double doublePrice
  ) throws Exception {

    ProductRequest productRequest = this.productRequestBuilder
      .withPrice(doublePrice).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("price"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(ints = {-1})
  @DisplayName("Should return bad request when the available quantity is null or negative")
  void shouldReturnBadRequestWhenTheAvailableQuantityIsNullOrZero(
    Integer availableQuantity
  ) throws Exception {

    ProductRequest productRequest = this.productRequestBuilder
      .withAvailableQuantity(availableQuantity).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("availableQuantity"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  @DisplayName("Should return bad request when the description is null, empty or blank")
  void shouldReturnBadRequestWhenTheDescriptionIsNullEmptyBlankOrHasMoreThanOneThousandCharacters(
    String description
  ) throws Exception {

    ProductRequest productRequest = this.productRequestBuilder
      .withDescription(description).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("description"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {999})
  @DisplayName("Should return bad request when the category id is null or does not exist")
  void shouldReturnBadRequestWhenTheCategoryIdIsNullOrDoesNotExist(
    Long categoryId
  ) throws Exception {

    ProductRequest productRequest = this.productRequestBuilder
      .withCategoryId(categoryId).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("categoryId"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());
  }

  @Test
  @DisplayName("Should return bad request when the description has more than one thousand characters")
  void shouldReturnBadRequestWhenTheDescriptionHasMoreThanOneThousandCharacters(

  ) throws Exception {

    ProductRequest productRequestA = this.productRequestBuilder
      .withDescription("a".repeat(1001)).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequestA))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(jsonPath("fieldErrors[0].name").value("description"))
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());

    ProductRequest productRequestB = this.productRequestBuilder
      .withDescription("a".repeat(1000)).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequestB))
      )
      .andExpect(status().isOk());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  @DisplayName("Should return bad request when one property name is null, empty or blank")
  void shouldReturnBadRequestWhenOnePropertyNameIsNullEmptyOrBlank(
    String propertyName
  ) throws Exception {

    PropertyDto invalidPropertyDto = new PropertyDto(
      propertyName, "property description"
    );

    PropertyDto propertyDtoA = new PropertyDto(
      "property name A", "property description"
    );

    PropertyDto propertyDtoB = new PropertyDto(
      "property name B", "property description"
    );

    ProductRequest productRequest = this.productRequestBuilder
      .withProperties(
        List.of(
          invalidPropertyDto,
          propertyDtoA,
          propertyDtoB
        )
      ).build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(
        jsonPath(
          "fieldErrors[0].name",
          matchesPattern("properties\\[[0-9]+]\\.name"
          )
        )
      )
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  @DisplayName("Should return bad request when one property description is null, empty or blank")
  void shouldReturnBadRequestWhenOnePropertyDescriptionIsNullEmptyOrBlank(
    String propertyDescription
  ) throws Exception {
    PropertyDto invalidPropertyDto = new PropertyDto(
      "property name", propertyDescription
    );

    PropertyDto propertyDtoA = new PropertyDto(
      "property name A", "property description"
    );

    PropertyDto propertyDtoB = new PropertyDto(
      "property name B", "property description"
    );

    ProductRequest productRequest = this.productRequestBuilder
      .withProperties(
        List.of(
          invalidPropertyDto,
          propertyDtoA,
          propertyDtoB
        )
      )
      .build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(
        jsonPath(
          "fieldErrors[0].name",
          matchesPattern("properties\\[[0-9]+]\\.description"))
      )
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());
  }

  @Test
  @DisplayName("Should return bad request when two properties have the same name")
  void shouldReturnBadRequestWhenTwoPropertiesHaveTheSameName(
  ) throws Exception {

    PropertyDto propertyDtoA = new PropertyDto(
      "property name", "property a description"
    );

    PropertyDto propertyDtoB = new PropertyDto(
      "property name", "property b description"
    );

    PropertyDto propertyDtoC = new PropertyDto(
      "property c name", "property c description"
    );

    ProductRequest productRequest = this.productRequestBuilder
      .withProperties(
        List.of(propertyDtoA, propertyDtoB, propertyDtoC)
      )
      .build();

    this.mockMvc
      .perform(
        post(this.urlTemplate)
          .header(this.jwtConfiguration.getHeader(), this.tokenWithPrefix)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("fieldErrors").isArray())
      .andExpect(jsonPath("fieldErrors.*", hasSize(1)))
      .andExpect(jsonPath("fieldErrors[0].*", hasSize(2)))
      .andExpect(
        jsonPath(
          "fieldErrors[0].name").value("properties")
      )
      .andExpect(jsonPath("fieldErrors[0].message").isString())
      .andExpect(jsonPath("fieldErrors[0].message").isNotEmpty());
  }

  @Test
  @DisplayName("Should return forbidden when JWT authorization token is not sent")
  void shouldReturnForbiddenWhenJwtAuthorizationTokenIsNotSent(
  ) throws Exception {

    ProductRequest productRequest = this.productRequestBuilder.build();

    this.mockMvc
      .perform(
        MockMvcRequestBuilders
          .post(this.urlTemplate)
          .contentType(MediaType.APPLICATION_JSON)
          .content(this.gson.toJson(productRequest))
      )
      .andExpect(MockMvcResultMatchers.status().isForbidden());

  }

  private static class ProductRequestBuilder {
    private String name = "product name";
    private BigDecimal price = BigDecimal.valueOf(1.2);
    private Integer availableQuantity = 2;
    private Collection<PropertyDto> properties;
    private String description = "product description";
    private Long categoryId;

    public ProductRequestBuilder(Long validCategoryId) {
      this.categoryId = validCategoryId;
      this.properties = this.setWithNProperties(3);
    }

    public ProductRequest build() {
      return new ProductRequest(
        this.name,
        this.price,
        this.availableQuantity,
        this.properties,
        this.description,
        this.categoryId
      );
    }

    public ProductRequestBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public ProductRequestBuilder withPrice(Double doublePrice) {
      BigDecimal bigDecimalPrice = null;

      if (null != doublePrice) {
        bigDecimalPrice = BigDecimal.valueOf(doublePrice);
      }

      this.price = bigDecimalPrice;
      return this;
    }

    public ProductRequestBuilder withAvailableQuantity(
      Integer availableQuantity
    ) {
      this.availableQuantity = availableQuantity;
      return this;
    }

    public ProductRequestBuilder withProperties(
      List<PropertyDto> properties
    ) {
      this.properties = properties;
      return this;
    }

    public ProductRequestBuilder withDescription(String description) {
      this.description = description;
      return this;
    }

    public ProductRequestBuilder withCategoryId(Long categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public ProductRequestBuilder withQuantityOfProperties(
      int quantityOfProperties
    ) {

      this.properties = this.setWithNProperties(
        quantityOfProperties
      );

      return this;
    }

    private Collection<PropertyDto> setWithNProperties(
      int quantityOfProperties
    ) {
      Collection<PropertyDto> properties = new HashSet<>();

      for (int i = 0; i < quantityOfProperties; i++) {

        properties.add(
          new PropertyDto("property name " + i, "property description")
        );

      }

      return properties;
    }

  }
}
