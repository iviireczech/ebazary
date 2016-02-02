package cz.ebazary.service.item.loaders;

import org.joda.time.LocalDate;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public interface Loadable {

    void loadItems(@NotNull LocalDate from);

}
