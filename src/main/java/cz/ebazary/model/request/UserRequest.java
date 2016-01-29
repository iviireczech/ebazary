package cz.ebazary.model.request;

import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.model.bazaar.locality.Region;
import lombok.Data;

@Data
public class UserRequest {
    private String query;
    private Category category;
    private Region region;
    private District district;
}
