package br.com.zupacademy.sergio.ecommerce;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

  @PersistenceContext
  private EntityManager entityManager;

  private Class<?> aClass;
  private String attributeName;

  @Override
  public void initialize(UniqueValue params) {
    this.aClass = params.domainClass();
    this.attributeName = params.fieldName();
  }

  @Override
  public boolean isValid(
    Object object, ConstraintValidatorContext constraintValidatorContext
  ) {
    return this.query(object).getResultList().isEmpty();
  }

  private Query query(Object object) {
    return this.entityManager
      .createQuery(this.queryClause())
      .setParameter("attributeValue", object);
  }

  private String queryClause() {
    return "SELECT 1 FROM " + this.aClass.getName() + " c WHERE " + this.attributeName + "=:attributeValue";
  }
}
