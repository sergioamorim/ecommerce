package br.com.zupacademy.sergio.ecommerce.security;

public class AccountCredentials {

  private String username;
  private String password;

  public AccountCredentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Deprecated
  private AccountCredentials() {  // jackson
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }
}
