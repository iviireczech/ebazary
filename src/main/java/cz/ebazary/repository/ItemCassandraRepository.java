package cz.ebazary.repository;

import cz.ebazary.model.item.Item;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCassandraRepository extends CassandraRepository<Item> {

}
