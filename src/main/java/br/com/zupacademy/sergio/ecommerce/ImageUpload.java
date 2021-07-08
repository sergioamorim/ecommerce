package br.com.zupacademy.sergio.ecommerce;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public class ImageUpload {
  public static Stream<String> uploadedImagesUrls(MultipartFile[] multipartImages) {

    return Arrays.stream(multipartImages)
      .map(multipartFile -> "https://fakecloud.com/file/" + UUID.randomUUID());

  }
}
