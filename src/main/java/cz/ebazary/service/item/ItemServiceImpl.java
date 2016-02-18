package cz.ebazary.service.item;

import cz.ebazary.converters.ItemToItemDTOConverter;
import cz.ebazary.dto.ItemDTO;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.model.item.Item;
import cz.ebazary.repository.ItemCassandraRepository;
import cz.ebazary.repository.ItemElasticsearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemElasticsearchRepository itemElasticsearchRepository;

    @Autowired
    private ItemCassandraRepository itemCassandraRepository;

    @Override
    public List<ItemDTO> getItems(final List<District> districts,
                                  final Category category,
                                  final String query) {

        final List<Item> items = itemElasticsearchRepository.findByDescription(query, new PageRequest(0, 20, null)).getContent();
        final Set<String> urls = items.stream().map(Item::getUrl).collect(Collectors.toSet());
        return ItemToItemDTOConverter.convert(itemCassandraRepository.findAll(urls));

    }
}
