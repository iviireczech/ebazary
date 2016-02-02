package cz.ebazary.repository;

import cz.ebazary.model.item.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends ElasticsearchRepository<Item, String> {

}
