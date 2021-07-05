package br.com.zupacademy.sergio.ecommerce.model.dto;

public class ClearPassword {
  private final String clearPassword;

  public ClearPassword(String clearPassword) {
    this.clearPassword = clearPassword;
  }

  public String get() {
    return this.clearPassword;
  }
}
