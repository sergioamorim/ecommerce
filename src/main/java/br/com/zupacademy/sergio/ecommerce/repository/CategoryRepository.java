package br.com.zupacademy.sergio.ecommerce.repository;

import br.com.zupacademy.sergio.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findByName(String name);

  int countByName(String name);
}
