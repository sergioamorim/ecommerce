package br.com.zupacademy.sergio.ecommerce.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtConfiguration {
  private String zoneOffset;
  private Integer daysToExpire;
  private String secret;
  private String tokenPrefix;
  private String header;

  public String getZoneOffset() {
    return this.zoneOffset;
  }

  public void setZoneOffset(String zoneOffset) {
    this.zoneOffset = zoneOffset;
  }

  public Integer getDaysToExpire() {
    return this.daysToExpire;
  }

  public void setDaysToExpire(Integer daysToExpire) {
    this.daysToExpire = daysToExpire;
  }

  public String getSecret() {
    return this.secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getTokenPrefix() {
    return this.tokenPrefix;
  }

  public void setTokenPrefix(String tokenPrefix) {
    this.tokenPrefix = tokenPrefix;
  }

  public String getHeader() {
    return this.header;
  }

  public void setHeader(String header) {
    this.header = header;
  }
}
