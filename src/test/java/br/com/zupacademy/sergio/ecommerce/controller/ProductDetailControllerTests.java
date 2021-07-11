package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.*;
import br.com.zupacademy.sergio.ecommerce.model.dto.EncodedPassword;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ReviewRepository;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductDetailControllerTests {
  private final MockMvc mockMvc;

  @Autowired
  public ProductDetailControllerTests(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  @DisplayName("Should return not found when the product id does not exist")
  void shouldReturnNotFoundWhenTheProductIdDoesNotExist() throws Exception {

    this.mockMvc
      .perform(get("/products/999"))
      .andExpect(status().isNotFound())
      .andExpect(content().string(""))
    ;

  }

  @Test
  @DisplayName("Should return ok and the product detail when the id is valid")
  void shouldReturnOkAndTheProductDetailWhenTheIdIsValid(
    @Autowired ProductRepository productRepository,
    @Autowired CategoryRepository categoryRepository,
    @Autowired ReviewRepository reviewRepository,
    @Autowired UserRepository userRepository
  ) throws Exception {

    Product product = productRepository.save(new Product(
      userRepository.save(new User(
        "g@ha.ir", new EncodedPassword("won't login")
      )),
      "product name",
      BigDecimal.ONE,
      1,
      List.of(
        new Property("property a", "description"),
        new Property("property b", "description"),
        new Property("property c", "description")
      ),
      "product description",
      categoryRepository.save(new Category("category name 38hf90j01"))
    ));

    reviewRepository.save(new Review(
      1,
      "review title",
      "review description",
      userRepository.save(new User(
        "h@ih.jp", new EncodedPassword("won't login")
      )),
      product
    ));

    this.mockMvc
      .perform(get("/products/" + product.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.*", hasSize(7)))
      .andExpect(jsonPath("images").isArray())
      .andExpect(jsonPath("name").isString())
      .andExpect(jsonPath("price").isNumber())
      .andExpect(jsonPath("properties").isArray())
      .andExpect(jsonPath("description").isString())
      .andExpect(jsonPath("questions").isArray())
      .andExpect(jsonPath("reviews.*", hasSize(3)))
      .andExpect(jsonPath("reviews.reviewCount").isNumber())
      .andExpect(jsonPath("reviews.ratingAverage").isNumber())
      .andExpect(jsonPath("reviews.reviews", hasSize(1)))
      .andExpect(jsonPath("reviews.reviews[0].*", hasSize(4)))
      .andExpect(jsonPath("reviews.reviews[0].rating").isNumber())
      .andExpect(jsonPath("reviews.reviews[0].title").isString())
      .andExpect(jsonPath("reviews.reviews[0].description").isString())
      .andExpect(jsonPath("reviews.reviews[0].userEmail").isString())
    ;

  }
}
