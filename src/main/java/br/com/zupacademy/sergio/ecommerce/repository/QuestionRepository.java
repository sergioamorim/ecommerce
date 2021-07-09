package br.com.zupacademy.sergio.ecommerce.repository;

import br.com.zupacademy.sergio.ecommerce.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
