package br.com.zupacademy.sergio.ecommerce.controller;

import br.com.zupacademy.sergio.ecommerce.model.dto.UserRequest;
import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import br.com.zupacademy.sergio.ecommerce.security.PasswordEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

  private final UserRepository userRepository;
  private final PasswordEncoding passwordEncoding;

  @Autowired
  public UserController(
    UserRepository userRepository, PasswordEncoding passwordEncoding
  ) {
    this.userRepository = userRepository;
    this.passwordEncoding = passwordEncoding;
  }

  @PostMapping("/users")
  public ResponseEntity<Object> createUser(
    @RequestBody @Valid UserRequest userRequest
  ) {
    this.userRepository.save(userRequest.toUser(this.passwordEncoding));
    return ResponseEntity.ok(null);
  }
}
