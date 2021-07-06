package br.com.zupacademy.sergio.ecommerce.model.dto;

public class EncodedPassword {
  private final String clearPassword;

  public EncodedPassword(String encodedPassword) {
    this.clearPassword = encodedPassword;
  }

  public String get() {
    return this.clearPassword;
  }
}
