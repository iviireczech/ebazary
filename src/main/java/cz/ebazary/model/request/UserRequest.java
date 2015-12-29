package cz.ebazary.model.request;

import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.model.bazaar.locality.Region;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserRequest {
    private String query;
    private Category category;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private Region region;
    private District district;
}
