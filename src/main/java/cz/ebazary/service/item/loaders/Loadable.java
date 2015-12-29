package cz.ebazary.service.item.loaders;

import cz.ebazary.model.item.Item;
import org.joda.time.LocalDate;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface Loadable {

    List<Item> loadItems(@NotNull LocalDate from);

}
