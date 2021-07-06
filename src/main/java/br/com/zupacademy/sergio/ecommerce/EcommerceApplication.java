package br.com.zupacademy.sergio.ecommerce;

import br.com.zupacademy.sergio.ecommerce.security.JwtConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtConfiguration.class})
public class EcommerceApplication {

  public static void main(String[] args) {
    SpringApplication.run(EcommerceApplication.class, args);
  }

}
