package cz.ebazary.model.bazaar.locality;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemLocalityValidator implements ConstraintValidator<Locality, ItemLocality> {

    @Override
    public void initialize(final Locality constraintAnnotation) {

    }

    @Override
    public boolean isValid(final ItemLocality value, final ConstraintValidatorContext context) {

        return value.getRegion() != null || value.getDistrict() != null;

    }
}
