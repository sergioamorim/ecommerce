package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.Image;
import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.User;
import br.com.zupacademy.sergio.ecommerce.model.dto.ImageDto;
import br.com.zupacademy.sergio.ecommerce.model.dto.UploadedImagesDto;
import br.com.zupacademy.sergio.ecommerce.repository.ImageRepository;
import br.com.zupacademy.sergio.ecommerce.repository.ProductRepository;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import br.com.zupacademy.sergio.ecommerce.validation.ForeignKeyExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static br.com.zupacademy.sergio.ecommerce.ImageUpload.uploadedImagesUrls;

@RestController
@Validated
public class ImageController {
  private final ImageRepository imageRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  @Autowired
  public ImageController(
    ImageRepository imageRepository,
    ProductRepository productRepository,
    UserRepository userRepository
  ) {
    this.imageRepository = imageRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
  }

  @PostMapping("/products/{productId}/images")
  public ResponseEntity<Object> createImage(
    @RequestParam MultipartFile[] multipartImages,
    @PathVariable @ForeignKeyExists(domainClass = Product.class) Long productId,
    @AuthenticationPrincipal String userEmail
  ) {

    return forbiddenOrOkAndNewImages(
      this.productRepository.findById(productId).get(),
      this.userRepository.findByEmail(userEmail).get(),
      multipartImages
    );
  }

  private ResponseEntity<Object> forbiddenOrOkAndNewImages(
    Product product, User user, MultipartFile[] multipartImages
  ) {
    if (!product.isOwnedBy(user)) {
      return ResponseEntity.status(403).build();
    }

    return ResponseEntity.ok(new UploadedImagesDto(
      uploadedImagesUrls(multipartImages)
        .map(url -> this.imageRepository.save(new Image(product, url)))
        .map(ImageDto::new)
    ));
  }
}
