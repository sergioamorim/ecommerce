package br.com.zupacademy.sergio.ecommerce;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcommerceApplicationTests {

  @Test
  @DisplayName("Context loads")
  void contextLoads() {
    EcommerceApplication.main(new String[0]);
  }

}
