package br.com.zupacademy.sergio.ecommerce.model.dto;

public class EncodedPassword {
  private final String encodedPassword;

  public EncodedPassword(String encodedPassword) {
    this.encodedPassword = encodedPassword;
  }

  public String get() {
    return this.encodedPassword;
  }
}
