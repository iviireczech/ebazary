package cz.ebazary.service.item.loaders;

import cz.ebazary.Application;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class HyperinzerceItemLoaderTest {

    @Autowired
    private Loadable hyperinzerceItemLoader;

    @Test
    public void testLoadItems() throws Exception {

        hyperinzerceItemLoader.loadItems(LocalDate.now().minusDays(1));

    }

}