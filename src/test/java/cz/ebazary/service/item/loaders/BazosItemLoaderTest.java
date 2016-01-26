package cz.ebazary.service.item.loaders;

import org.joda.time.LocalDate;
import org.junit.Test;

public class BazosItemLoaderTest {

    @Test
    public void testLoadItems() throws Exception {
        final Loadable loadable = new BazosItemLoader();
        loadable.loadItems(LocalDate.now().minusDays(7));
    }
}