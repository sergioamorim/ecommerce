package br.com.zupacademy.sergio.ecommerce.model;

import br.com.zupacademy.sergio.ecommerce.model.dto.EncodedPassword;
import org.hibernate.annotations.CreationTimestamp;

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

  public User(String email, EncodedPassword encodedPassword) {
    this.email = email;
    this.password = encodedPassword.get();
  }

  @Deprecated  // jpa
  protected User() {
  }

  public Long getId() {
    return this.id;
  }

  public String getEmail() {
    return this.email;
  }

  public String getPassword() {
    return this.password;
  }

  public ZonedDateTime getCreation() {
    return this.creation;
  }
}
