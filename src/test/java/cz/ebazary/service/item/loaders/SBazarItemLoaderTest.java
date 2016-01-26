package cz.ebazary.service.item.loaders;

import org.joda.time.LocalDate;
import org.junit.Test;

public class SBazarItemLoaderTest {

    @Test
    public void testLoadItems() throws Exception {
        final Loadable loadable = new SBazarItemLoader();
        loadable.loadItems(LocalDate.now().minusDays(1));
    }

}