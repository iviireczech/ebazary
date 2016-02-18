package cz.ebazary.repository;

import cz.ebazary.model.item.Item;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface ItemElasticsearchRepository extends ElasticsearchRepository<Item, String> {

    Page<Item> findByDescription(@NotBlank String description,
                                 @NotNull Pageable pageable);

}
