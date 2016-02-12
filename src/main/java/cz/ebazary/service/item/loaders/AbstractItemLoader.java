package cz.ebazary.service.item.loaders;

import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.item.Item;
import cz.ebazary.repository.ItemCassandraRepository;
import cz.ebazary.repository.ItemElasticsearchRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Date;
import java.util.List;

public abstract class AbstractItemLoader implements Loadable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractItemLoader.class);

    @Autowired
    private Validator validator;

    @Autowired
    private ItemElasticsearchRepository itemElasticsearchRepository;

    @Autowired
    private ItemCassandraRepository itemCassandraRepository;

    @Override
    public void loadItems(final Date from) {

        int itemCounter = 0;

        for (Category category : Category.values()) {
            final List<String> categoryUrls = getCategoryUrls(category);
            for (final String categoryUrl : categoryUrls) {
                Date insertionDate = new Date();
                int page = 0;
                while (!insertionDate.before(from)) {

                    final List<String> itemUrls;
                    try {
                        final String categoryPageUrl = getCategoryPageUrl(categoryUrl, page++);
                        final Document categoryPage = Jsoup.connect(categoryPageUrl).timeout(5000).get();
                        itemUrls = getItemUrls(categoryPage);
                    } catch (Exception e) {
                        LOGGER.error(e.toString());
                        break;
                    }

                    if (itemUrls.isEmpty()) {
                        break;
                    }

                    for (String itemUrl : itemUrls) {
                        try {
                            final Document itemPage = Jsoup.connect(itemUrl).timeout(5000).get();

                            final Item item = getItem(itemPage);
                            item.setCategory(category.name());

                            validator.validate(item);

                            insertionDate = item.getInsertionDate();

                            if (insertionDate.before(from)) break;

                            itemElasticsearchRepository.index(item);
                            itemCassandraRepository.save(item);

                            if (++itemCounter % 1000 == 0) {
                                LOGGER.info("" + itemCounter);
                                return;
                            }

                        } catch (Exception e) {
                            if (e instanceof ConstraintViolationException) {
                                final ConstraintViolationException cve = (ConstraintViolationException) e;
                                for (ConstraintViolation<?> constraint : cve.getConstraintViolations()) {
                                    LOGGER.error(
                                            constraint.getPropertyPath()
                                                    + " " + constraint.getMessage()
                                                    + "( " + ((Item)constraint.getRootBean()).getUrl() + " )"
                                    );
                                }
                            } else {
                                LOGGER.error(e.getMessage());
                            }
                        }

                    }
                }
            }

        }

    }

    protected abstract List<String> getCategoryUrls(final Category category);

    protected abstract String getCategoryPageUrl(final String categoryUrl, final int page);

    protected abstract List<String> getItemUrls(final Document categoryPage);

    protected abstract Item getItem(final Document itemPage);

    protected boolean isNumeric(String str) {
        return str.matches("\\d+((\\.|,)\\d+)?");
    }

}
