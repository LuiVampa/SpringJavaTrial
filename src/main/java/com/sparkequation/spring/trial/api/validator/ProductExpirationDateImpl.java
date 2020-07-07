package com.sparkequation.spring.trial.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ProductExpirationDateImpl implements ConstraintValidator<ProductExpirationDate, Date> {

    @Override
    public boolean isValid(Date productExpirationDate, ConstraintValidatorContext context) {
        return productExpirationDate == null
               || productExpirationDate.after(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)));
    }
}
