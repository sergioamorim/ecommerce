package br.com.zupacademy.sergio.ecommerce.model.dto;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UploadedImagesDto {
  private final Collection<ImageDto> uploadedImages;

  public UploadedImagesDto(Stream<ImageDto> uploadedImages) {
    this.uploadedImages = uploadedImages.collect(Collectors.toList());
  }

  public Collection<ImageDto> getUploadedImages() {
    return this.uploadedImages;
  }
}
