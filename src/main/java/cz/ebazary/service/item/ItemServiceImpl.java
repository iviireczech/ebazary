package cz.ebazary.service.item;

import cz.ebazary.converters.ItemToItemDTOConverter;
import cz.ebazary.dto.ItemDTO;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.repository.ItemElasticsearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemElasticsearchRepository itemElasticsearchRepository;

    @Override
    public List<ItemDTO> getItems(final List<District> districts,
                                  final Category category,
                                  final String query) {

        return ItemToItemDTOConverter.convert(itemElasticsearchRepository.findAll());

    }
}
