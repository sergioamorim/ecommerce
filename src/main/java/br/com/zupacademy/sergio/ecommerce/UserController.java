package br.com.zupacademy.sergio.ecommerce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

  private final UserRepository userRepository;

  @Autowired
  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping("/users")
  public ResponseEntity<Object> createUser(
    @RequestBody @Valid UserRequest userRequest
  ) {
    this.userRepository.save(userRequest.toUser());
    return ResponseEntity.ok(null);
  }
}
