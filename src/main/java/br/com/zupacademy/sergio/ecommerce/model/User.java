package br.com.zupacademy.sergio.ecommerce.model;

import br.com.zupacademy.sergio.ecommerce.model.dto.ClearPassword;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
public class User {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @CreationTimestamp
  private ZonedDateTime creation;

  public User(String email, ClearPassword clearPassword) {
    this.email = email;
    this.password = BCrypt.hashpw(clearPassword.get(), BCrypt.gensalt());
  }

  @Deprecated  // jpa
  protected User() {
  }

  public String getPassword() {
    return this.password;
  }

  public ZonedDateTime getCreation() {
    return this.creation;
  }
}
