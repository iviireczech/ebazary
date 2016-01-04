package cz.ebazary.model.item;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemPriceValidator implements ConstraintValidator<Price, ItemPrice> {

    @Override
    public void initialize(final Price constraintAnnotation) {

    }

    @Override
    public boolean isValid(final ItemPrice value, final ConstraintValidatorContext context) {

        return value.getCurrency() != null && (value.getPrice() != null || value.isNegotiatedPrice() || value.isPriceInDescription());

    }
}
