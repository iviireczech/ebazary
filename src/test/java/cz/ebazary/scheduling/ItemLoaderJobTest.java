package cz.ebazary.scheduling;

import cz.ebazary.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class ItemLoaderJobTest {

    @Autowired
    private ItemLoaderJob itemLoaderJob;

    @Test
    public void testLoadNewItems() throws Exception {
        itemLoaderJob.loadNewItems();
    }
}