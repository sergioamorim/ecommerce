package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.security.PasswordEncoding;
import br.com.zupacademy.sergio.ecommerce.validation.UniqueValue;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class UserRequest {

  @Email
  @NotEmpty
  @UniqueValue(domainClass = User.class, fieldName = "email")
  private final String email;

  @NotBlank
  @Length(min = 6)
  private final String password;

  public UserRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public User toUser(PasswordEncoding passwordEncoding) {
    return new User(
      this.email,
      new EncodedPassword(passwordEncoding.encode(this.password))
    );
  }
}
