package cz.ebazary.service.item.loaders;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Validated
public interface Loadable {

    void loadItems(@NotNull Date from);

}
