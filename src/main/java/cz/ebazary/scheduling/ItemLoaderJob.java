package cz.ebazary.scheduling;

import cz.ebazary.service.item.loaders.Loadable;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemLoaderJob {

    @Autowired
    private List<Loadable> loadables;

    //@Scheduled(cron = "0 0 0 * * *")
    public void loadNewItems() {
        loadables
                .stream()
                .forEach(loadable -> loadable.loadItems(LocalDate.now()));
    }

}
