package cz.ebazary.service.item;

import cz.ebazary.dto.ItemDTO;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface ItemService {

    List<ItemDTO> getItems(List<District> districts,
                           Category category,
                           String query);

}
