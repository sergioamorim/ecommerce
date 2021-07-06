package br.com.zupacademy.sergio.ecommerce.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoding {
  final PasswordEncoder passwordEncoder;

  public PasswordEncoding() {
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public PasswordEncoder passwordEncoder() {
    return this.passwordEncoder;
  }

  public String encode(String password) {
    return this.passwordEncoder.encode(password);
  }
}
