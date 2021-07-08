package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Image;

public class ImageDto {
  private final String url;

  public ImageDto(Image image) {
    this.url = image.getUrl();
  }

  public String getUrl() {
    return this.url;
  }
}
