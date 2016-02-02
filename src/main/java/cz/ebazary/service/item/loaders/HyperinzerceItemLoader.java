package cz.ebazary.service.item.loaders;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.model.item.Item;
import cz.ebazary.model.item.ItemCurrency;
import cz.ebazary.model.item.ItemPrice;
import cz.ebazary.utils.DisctrictUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class HyperinzerceItemLoader extends AbstractItemLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HyperinzerceItemLoader.class);

    private static final String DETAIL_SELECTOR = "div.show-menu-offer-1";
    private static final String DATE_SELECTOR = "span.pr5.f12";
    private static final String MAIN_IMAGE_SELECTOR = "a[rel=photos]";
    private static final String OTHER_IMAGES_SELECTOR = "div#photos-show > a[rel=photos]";
    private static final String PRICE_SELECTOR = "p.f18.b.c-org1.w185.blk.fr";
    private static final String DESCRIPTION_SELECTOR = "div.w284.mt20.ww.f14.c-blck.lh20.fl";
    private static final String PHONE_SELECTOR = "table.inz-kont > tbody > tr";
    private static final String LOCALITY_SELECTOR = "p.w185.blk.fr.lh25.h25 > a.ul";

    private static final String PRICE_NOT_LISTED = "neuvedeno";

    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final String TODAY = "dnes";
    private static final String YESTERDAY = "včera";

    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d+)(.*)");
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("([0-9]{2}.[0-9]{2}.|dnes|včera)[0-9]{2}:[0-9]{2}");
    private static final Pattern ONLY_DATE_PATTERN = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}");

    private static final Map<Category, List<String>> CATEGORY_URLS = new HashMap<>();

    static {
        final List<String> carsUrls = new ArrayList<>();
        carsUrls.add("http://autobazar.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.cars, carsUrls);

        final List<String> motorcyclesUrls = new ArrayList<>();
        motorcyclesUrls.add("http://motorky.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.motorcycles, motorcyclesUrls);

        final List<String> machinesUrls = new ArrayList<>();
        machinesUrls.add("http://stroje.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.machines, machinesUrls);

        final List<String> childrenUrls = new ArrayList<>();
        childrenUrls.add("http://detsky-bazar.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.children, childrenUrls);

        final List<String> homeUrls = new ArrayList<>();
        homeUrls.add("http://stavba.hyperinzerce.cz/inzerce/nabidka");
        homeUrls.add("http://zahrada.hyperinzerce.cz/inzerce/nabidka");
        homeUrls.add("http://nabytek-bydleni.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.home, homeUrls);

        final List<String> animalsUrls = new ArrayList<>();
        animalsUrls.add("http://zvirata.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.animals, animalsUrls);

        final List<String> sportUrls = new ArrayList<>();
        sportUrls.add("http://sport.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.sport, sportUrls);

        final List<String> cultureUrls = new ArrayList<>();
        cultureUrls.add("http://kultura.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.culture, cultureUrls);

        final List<String> booksUrls = new ArrayList<>();
        booksUrls.add("http://knihy.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.books, booksUrls);

        final List<String> fashionUrls = new ArrayList<>();
        fashionUrls.add("http://obleceni-obuv.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.fashion, fashionUrls);

        final List<String> computersUrls = new ArrayList<>();
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-notebooky-znacky/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-notebooky-druhy/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-notebooky-prislusenstvi/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-notebooky-dily/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-pc-sestavy/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-monitory/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-tiskarny/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-periferni-zarizeni/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-sitove-prvky/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-software/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-pc-hry/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-herni-konzole/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-hry-na-konzole/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-servery/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-servery-dily/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-pocitace-sluzby/inzerce");
        computersUrls.add("http://pocitace.hyperinzerce.cz/inzerce-pocitace-ostatni/inzerce");
        CATEGORY_URLS.put(Category.computers, computersUrls);

        final List<String> mobileDevicesUrls = new ArrayList<>();
        mobileDevicesUrls.add("http://mobily.hyperinzerce.cz/inzerce/nabidka");
        mobileDevicesUrls.add("http://pocitace.hyperinzerce.cz/inzerce-pda-ebooky/inzerce/nabidka");
        CATEGORY_URLS.put(Category.mobile_devices, mobileDevicesUrls);

        final List<String> appliancesUrls = new ArrayList<>();
        appliancesUrls.add("http://elektro.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.appliances, appliancesUrls);

        final List<String> audioVideoUrls = new ArrayList<>();
        audioVideoUrls.add("http://audio-video-tv.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.audio_video, audioVideoUrls);

        final List<String> photoUrls = new ArrayList<>();
        photoUrls.add("http://foto.hyperinzerce.cz/inzerce/nabidka");
        CATEGORY_URLS.put(Category.photo, photoUrls);
    }

    @Override
    protected List<String> getCategoryUrls(final Category category) {

        final List<String> categoryUrls = CATEGORY_URLS.get(category);
        return categoryUrls == null ? new ArrayList<>() : categoryUrls;

    }

    @Override
    protected String getCategoryPageUrl(final String categoryUrl, final int page) {

        return categoryUrl + "/" + (page + 1) + "/";

    }

    @Override
    protected List<String> getItemUrls(final Document categoryPage) {

        final List<String> itemUrls = new ArrayList<>();

        final Elements details = categoryPage.select(DETAIL_SELECTOR);
        for (Element detail : details) {
            final Elements links = detail.select("h3 > a");
            itemUrls.addAll(links.stream().map(link -> link.attr("href")).collect(Collectors.toList()));
        }

        return itemUrls;

    }

    @Override
    protected Item getItem(final Document itemPage) {

        final Item item = new Item();
        item.setBazaarType(BazaarType.hyperinzerce);
        item.setUrl(itemPage.location());

        setPrice(itemPage, item);
        setDescription(itemPage, item);
        setPhone(itemPage, item);
        //email not supported
        //setEmail(itemPage, item);
        setLocality(itemPage, item);
        setInsertionDate(itemPage, item);
        setMainImageUrl(itemPage, item);
        setOtherImagesUrl(itemPage, item);
        return item;

    }

    private void setPrice(final Document document, final Item item) {

        final ItemPrice itemPrice = new ItemPrice();

        final String price = StringUtils.trimAllWhitespace(document.select(PRICE_SELECTOR).text());

        if(PRICE_NOT_LISTED.matches(price)) {
            itemPrice.setNegotiatedPrice(true);
        } else {
            final Matcher matcher = PRICE_PATTERN.matcher(price);
            if (matcher.matches()) {
                if (isNumeric(matcher.group(1))) {
                    itemPrice.setPrice(new BigDecimal(matcher.group(1)));

                    itemPrice.setCurrency(
                            ItemCurrency
                                    .findByName(matcher.group(2))
                                    .orElseThrow(() -> new IllegalArgumentException("Currency " + matcher.group(2) + " not recognized"))
                    );

                }
            }
        }

        if (itemPrice.getPrice() == null && !itemPrice.isNegotiatedPrice() && !itemPrice.isPriceInDescription()) {
            throw new IllegalStateException(price);
        }

        item.setItemPrice(itemPrice);

    }

    private void setDescription(final Document document, final Item item) {

        final Element description = document.select(DESCRIPTION_SELECTOR).first();
        description.child(0).remove();
        item.setDescription(description.text());

        if (StringUtils.isEmpty(item.getDescription())) {
            LOGGER.warn("Empty description");
        }

    }

    private void setPhone(final Document document, final Item item) {

        final Elements trs = document.select(PHONE_SELECTOR);
        if (trs.size() > 1) {
            final String phone = trs.get(1).select("td:not(.w75.c-lblue)").text();
            item.setPhoneNumber(StringUtils.isEmpty(phone) ? null : phone);
        }

        if (StringUtils.isEmpty(item.getPhoneNumber())) {
            LOGGER.warn("Empty phone number");
        }

    }

    private void setLocality(final Document document, final Item item) {

        final Elements locationElement = document.select(LOCALITY_SELECTOR);
        final String localityString = locationElement.select("a").text();

        final List<District> districts = DisctrictUtil.getDistricts(localityString);
        if (CollectionUtils.isEmpty(districts)) {
            throw new IllegalStateException("Unsupported location " + localityString);
        }

        item.getDistricts().addAll(districts);

    }

    private void setInsertionDate(final Document document, final Item item) {

        final String date = StringUtils.trimAllWhitespace(document.select(DATE_SELECTOR).text());
        final Matcher dateTimeMatcher = DATE_TIME_PATTERN.matcher(date);
        final Matcher dateMatcher = ONLY_DATE_PATTERN.matcher(date);

        final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_PATTERN);
        if (dateTimeMatcher.matches()) {
            final LocalDate localDate;
            if (TODAY.equals(dateTimeMatcher.group(1))) {
                localDate = LocalDate.now();
            } else if (YESTERDAY.equals(dateTimeMatcher.group(1))) {
                localDate = LocalDate.now().minusDays(1);
            } else {
                localDate = formatter.parseLocalDate(dateTimeMatcher.group(1) + DateTime.now().getYear());
            }
            item.setInsertionDate(localDate);
        } else if (dateMatcher.matches()) {
            final LocalDate localDate = formatter.parseLocalDate(date);
            item.setInsertionDate(localDate);
        }

        if (item.getInsertionDate() == null) {
            LOGGER.warn("Empty insertion date");
        }

    }

    private void setMainImageUrl(final Document document, final Item item) {

        final Elements images = document.select(MAIN_IMAGE_SELECTOR);
        if(!images.isEmpty()) {
            item.setMainImageUrl(images.first().attr("href"));
        }

        if (StringUtils.isEmpty(item.getMainImageUrl())) {
            LOGGER.warn("Empty main image url");
        }

    }

    private void setOtherImagesUrl(final Document document, final Item item) {

        final Elements images = document.select(OTHER_IMAGES_SELECTOR);
        if(!images.isEmpty()) {
            item.setMainImageUrl(images.first().attr("href"));
        }

        if (CollectionUtils.isEmpty(item.getOtherImagesUrl())) {
            LOGGER.warn("Empty other image urls");
        }

    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+((\\.|,)\\d+)?");
    }

}
