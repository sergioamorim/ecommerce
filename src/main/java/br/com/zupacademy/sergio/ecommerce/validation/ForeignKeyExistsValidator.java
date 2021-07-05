package br.com.zupacademy.sergio.ecommerce.validation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ForeignKeyExistsValidator implements ConstraintValidator<ForeignKeyExists, Object> {
  @PersistenceContext
  private EntityManager entityManager;

  private Class<?> aClass;
  private boolean nullable;

  @Override
  public void initialize(ForeignKeyExists params) {
    this.aClass = params.domainClass();
    this.nullable = params.nullable();
  }

  @Override
  public boolean isValid(
    Object object, ConstraintValidatorContext constraintValidatorContext
  ) {

    if (null == object && this.nullable) {
      return true;
    }

    return !this.query(object).getResultList().isEmpty();
  }

  private Query query(Object object) {
    return this.entityManager.createQuery(this.queryClause()).setParameter("objectId", object);
  }

  private String queryClause() {
    return "SELECT 1 FROM " + this.aClass.getName() + " WHERE id=:objectId";
  }
}
