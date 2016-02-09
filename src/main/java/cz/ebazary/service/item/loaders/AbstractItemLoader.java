package cz.ebazary.service.item.loaders;

import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.item.Item;
import cz.ebazary.repository.ItemRepository;
import org.joda.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class AbstractItemLoader implements Loadable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractItemLoader.class);

    @Autowired
    private Validator validator;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void loadItems(final LocalDate from) {

        try {

            for (Category category : Category.values()) {
                final List<String> categoryUrls = getCategoryUrls(category);
                for (final String categoryUrl : categoryUrls) {
                    LocalDate insertionDate = LocalDate.now();
                    int page = 0;
                    while (!insertionDate.isBefore(from)) {
                        final String categoryPageUrl = getCategoryPageUrl(categoryUrl, page++);
                        final Document categoryPage = Jsoup.connect(categoryPageUrl).timeout(5000).get();
                        final List<String> itemUrls = getItemUrls(categoryPage);
                        for (String itemUrl : itemUrls) {
                            final Document itemPage = Jsoup.connect(itemUrl).timeout(5000).get();

                            final Item item = getItem(itemPage);
                            item.setCategory(category);
                            final Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);

                            if (!constraintViolations.isEmpty()) {
                                for (ConstraintViolation<Item> constraints : constraintViolations) {
                                    LOGGER.error(
                                            constraints.getPropertyPath()
                                                    + " " + constraints.getMessage()
                                                    + "( " + item.getUrl() + " )"
                                    );
                                }
                                throw new ConstraintViolationException(constraintViolations);
                            }

                            insertionDate = item.getInsertionDate();

                            if (insertionDate.isBefore(from)) break;

                            itemRepository.index(item);

                        }
                    }
                }

            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
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
