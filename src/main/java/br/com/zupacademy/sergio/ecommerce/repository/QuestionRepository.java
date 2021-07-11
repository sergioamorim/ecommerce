package br.com.zupacademy.sergio.ecommerce.repository;

import br.com.zupacademy.sergio.ecommerce.model.Product;
import br.com.zupacademy.sergio.ecommerce.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
  Stream<Question> streamAllByProduct(Product product);
}
