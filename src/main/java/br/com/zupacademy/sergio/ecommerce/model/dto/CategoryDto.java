package br.com.zupacademy.sergio.ecommerce.model.dto;

import br.com.zupacademy.sergio.ecommerce.model.Category;
import br.com.zupacademy.sergio.ecommerce.repository.CategoryRepository;
import br.com.zupacademy.sergio.ecommerce.validation.ForeignKeyExists;
import br.com.zupacademy.sergio.ecommerce.validation.UniqueValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class CategoryDto {

  @NotBlank
  @UniqueValue(domainClass = Category.class, fieldName = "name")
  @JsonProperty
  private final String name;

  @ForeignKeyExists(domainClass = Category.class)
  @JsonProperty
  private final Long parentId;

  @JsonCreator
  public CategoryDto(String name, Long parentId) {
    this.name = name;
    this.parentId = parentId;
  }

  public CategoryDto(Category category) {
    this.name = category.getName();
    this.parentId = category.getParentId();
  }

  public Category toCategory(CategoryRepository categoryRepository) {
    if (null != this.parentId) {
      return new Category(
        this.name, categoryRepository.findById(this.parentId).get()
      );
    }
    return new Category(this.name);
  }
}
