package cz.ebazary.scheduling;

import cz.ebazary.service.item.loaders.Loadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class ItemLoaderJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemLoaderJob.class);

    @Autowired
    private List<Loadable> loadables;

    @Scheduled(fixedDelay = 100_000)
    public void loadNewItems() {

        // today
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        loadables
                .stream()
                .forEach(loadable -> {
                    LOGGER.info("Loading items using: " + loadable.getClass().getSimpleName());
                    loadable.loadItems(date.getTime());
                });
    }

}
