package cz.ebazary.service.item.loaders;

import org.joda.time.LocalDate;
import org.junit.Test;

public class BazarItemLoaderTest {

    @Test
    public void testLoadItems() throws Exception {

        final Loadable loadable = new BazarItemLoader();
        loadable.loadItems(LocalDate.now().minusDays(1));

    }
}