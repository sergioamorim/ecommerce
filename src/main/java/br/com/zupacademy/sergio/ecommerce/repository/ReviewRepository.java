package br.com.zupacademy.sergio.ecommerce.repository;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  Stream<Review> streamAllByProduct(Product product);
}
