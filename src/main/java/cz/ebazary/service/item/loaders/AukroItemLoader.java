package cz.ebazary.service.item.loaders;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.ItemLocality;
import cz.ebazary.model.bazaar.locality.Region;
import cz.ebazary.model.item.Item;
import cz.ebazary.model.item.ItemCurrency;
import cz.ebazary.model.item.ItemPrice;
import cz.ebazary.utils.ItemLocalityUtil;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AukroItemLoader extends AbstractItemLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AukroItemLoader.class);

    private static final String DETAIL_SELECTOR = "div.show-menu-offer-1";
    private static final String DATE_SELECTOR = "li.timeInfo";
    private static final String MAIN_IMAGE_SELECTOR = "meta[itemprop=image]";
    private static final String OTHER_IMAGES_SELECTOR = "script#pinTemplate";
    private static final String PRICE_SELECTOR = "strong#priceValue";
    private static final String DESCRIPTION_SELECTOR = "div.main-title > h1";
    private static final String PHONE_SELECTOR = "table.inz-kont > tbody > tr";
    private static final String LOCALITY_SELECTOR = "div#paymentShipment > p.small > strong";

    private static final String PRICE_NOT_LISTED = "neuvedeno";

    private static final String DATE_PATTERN = "dd MMMM y";
    private static final String TODAY = "dnes";
    private static final String YESTERDAY = "včera";

    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d+)(.*)");
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile(" čas do konce \\(.*, ([0-9]{2} .*), .*\\) ");
    private static final Pattern OTHER_IMAGES_PATTERN = Pattern.compile("\\{\"small\":\\[.*\\],\"medium\":\\[.*\\],\"large\":\\[(.*)\\]\\}");
    private static final Pattern ONLY_DATE_PATTERN = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}");

    private static final Map<Category, List<String>> CATEGORY_URLS = new HashMap<>();

    static {
        final List<String> carsUrls = new ArrayList<>();
        carsUrls.add("http://aukro.cz/automobily-8503");
        carsUrls.add("http://aukro.cz/nahradni-dily-prislusenstvi-pro-osobni-vozidla-8500");
        carsUrls.add("http://aukro.cz/prislusenstvi-k-veteranum-48796");
        carsUrls.add("http://aukro.cz/pneumatiky-110064");
        carsUrls.add("http://aukro.cz/privesy-navesy-73127");
        CATEGORY_URLS.put(Category.cars, carsUrls);

        final List<String> motorcyclesUrls = new ArrayList<>();
        motorcyclesUrls.add("http://aukro.cz/motocykly-8502");
        motorcyclesUrls.add("http://aukro.cz/nahradni-dily-prislusenstvi-pro-motocykly-48787");
        CATEGORY_URLS.put(Category.motorcycles, motorcyclesUrls);

        final List<String> machinesUrls = new ArrayList<>();
        machinesUrls.add("http://aukro.cz/pracovni-a-zemedelske-stroje-73128");
        CATEGORY_URLS.put(Category.machines, machinesUrls);

        final List<String> childrenUrls = new ArrayList<>();
        childrenUrls.add("http://aukro.cz/hracky-144281");
        childrenUrls.add("http://aukro.cz/obleceni-143822");
        childrenUrls.add("http://aukro.cz/detsky-pokoj-144369");
        childrenUrls.add("http://aukro.cz/kocarky-144355");
        childrenUrls.add("http://aukro.cz/pece-o-kojence-a-batolata-144386");
        childrenUrls.add("http://aukro.cz/autosedacky-144347");
        CATEGORY_URLS.put(Category.children, childrenUrls);

        final List<String> homeUrls = new ArrayList<>();
        homeUrls.add("http://aukro.cz/nabytek-8487");
        homeUrls.add("http://aukro.cz/zarizeni-8490");
        homeUrls.add("http://aukro.cz/naradi-8482");
        homeUrls.add("http://aukro.cz/zabezpecovaci-systemy-10883");
        homeUrls.add("http://aukro.cz/stavebniny-8485");
        homeUrls.add("http://aukro.cz/zahrada-8483");
        homeUrls.add("http://aukro.cz/ostatni-10201");
        homeUrls.add("http://aukro.cz/gastronomie-hotel-11956");
        homeUrls.add("http://aukro.cz/kancelar-11953");
        homeUrls.add("http://aukro.cz/stavba-11954");
        CATEGORY_URLS.put(Category.home, homeUrls);

        final List<String> animalsUrls = new ArrayList<>();
        animalsUrls.add("http://aukro.cz/zvirata-8484");
        CATEGORY_URLS.put(Category.animals, animalsUrls);

        final List<String> sportUrls = new ArrayList<>();
        sportUrls.add("http://aukro.cz/sport-a-turistika");
        sportUrls.add("http://aukro.cz/zabava");
        CATEGORY_URLS.put(Category.sport, sportUrls);

        final List<String> cultureUrls = new ArrayList<>();
        cultureUrls.add("http://aukro.cz/hudba-a-film");
        CATEGORY_URLS.put(Category.culture, cultureUrls);

        final List<String> booksUrls = new ArrayList<>();
        booksUrls.add("http://aukro.cz/knihy-a-casopisy");
        CATEGORY_URLS.put(Category.books, booksUrls);

        final List<String> fashionUrls = new ArrayList<>();
        fashionUrls.add("http://aukro.cz/obleceni-obuv-a-doplnky");
        fashionUrls.add("http://aukro.cz/sperky-a-hodinky");
        CATEGORY_URLS.put(Category.fashion, fashionUrls);

        final List<String> computersUrls = new ArrayList<>();
        computersUrls.add("http://aukro.cz/notebooky-prislusenstvi-100697");
        computersUrls.add("http://aukro.cz/stolni-pocitace-100696");
        computersUrls.add("http://aukro.cz/pc-komponenty-100701");
        computersUrls.add("http://aukro.cz/prislusenstvi-k-pc-100702");
        computersUrls.add("http://aukro.cz/software-100704");
        computersUrls.add("http://aukro.cz/herni-zarizeni-100699");
        computersUrls.add("http://aukro.cz/hry-100705");
        computersUrls.add("http://aukro.cz/servery-prislusenstvi-100698");
        computersUrls.add("http://aukro.cz/historicke-pocitace-100703");
        computersUrls.add("http://aukro.cz/ostatni-100706");
        CATEGORY_URLS.put(Category.computers, computersUrls);

        final List<String> mobileDevicesUrls = new ArrayList<>();
        mobileDevicesUrls.add("http://aukro.cz/mobily-a-gps");
        mobileDevicesUrls.add("http://aukro.cz/tablety-a-ctecky-e-knih-100700");
        CATEGORY_URLS.put(Category.mobile_devices, mobileDevicesUrls);

        final List<String> appliancesUrls = new ArrayList<>();
        appliancesUrls.add("http://aukro.cz/elektronika-100898");
        appliancesUrls.add("http://aukro.cz/male-elektrospotrebice-100892");
        appliancesUrls.add("http://aukro.cz/velke-elektrospotrebice-100891");
        appliancesUrls.add("http://aukro.cz/pece-o-telo-100895");
        appliancesUrls.add("http://aukro.cz/zaznamova-media-100897");
        appliancesUrls.add("http://aukro.cz/vzduchotechnika-topeni-100896");
        appliancesUrls.add("http://aukro.cz/ostatni-100899");
        CATEGORY_URLS.put(Category.appliances, appliancesUrls);

        final List<String> audioVideoUrls = new ArrayList<>();
        audioVideoUrls.add("http://aukro.cz/tv-audio-video-100893");
        CATEGORY_URLS.put(Category.audio_video, audioVideoUrls);

        final List<String> photoUrls = new ArrayList<>();
        photoUrls.add("http://aukro.cz/foto-100894");
        CATEGORY_URLS.put(Category.photo, photoUrls);
    }

    @Override
    protected List<String> getCategoryUrls(final Category category) {

        final List<String> categoryUrls = CATEGORY_URLS.get(category);
        return categoryUrls == null ? new ArrayList<>() : categoryUrls;

    }

    @Override
    protected String getCategoryPageUrl(final String categoryUrl, final int page) {

        return categoryUrl + "?p=" + (page + 1);

    }

    @Override
    protected List<String> getItemUrls(final Document categoryPage) {

        final List<String> itemUrls = new ArrayList<>();

        final Elements links = categoryPage.select("div.details > header > h2 > a");
        for (Element link : links) {
            itemUrls.add("http://www.aukro.cz" + link.attr("href"));
        }

        return itemUrls;

    }

    @Override
    protected Item getItem(final Document itemPage) {

        final Item item = new Item();
        item.setBazaarType(BazaarType.aukro);
        item.setUrl(itemPage.location());

        setPrice(itemPage, item);
        setDescription(itemPage, item);
        //phone not supported
        //setPhone(itemPage, item);
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

        final String price = StringUtils.trimAllWhitespace(document.select(PRICE_SELECTOR).first().text());

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

        if (itemPrice.getPrice() == null && !itemPrice.isNegotiatedPrice() && !itemPrice.isPriceInDescription()) {
            throw new IllegalStateException(price);
        }

        item.setItemPrice(itemPrice);

    }

    private void setDescription(final Document document, final Item item) {

        final Elements description = document.select(DESCRIPTION_SELECTOR);
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
        final String localityString = locationElement.text();

        final ItemLocality itemLocality =
                ItemLocalityUtil
                        .getItemLocality(Region.CZ.getName())
                        .orElseThrow(() -> new IllegalStateException("Unsupported location " + localityString));

        item.setItemLocality(itemLocality);

    }

    private void setInsertionDate(final Document document, final Item item) {

        final Element dateElement = document.select(DATE_SELECTOR).first();
        final String date = dateElement.textNodes().get(1).text();
        final Matcher dateTimeMatcher = DATE_TIME_PATTERN.matcher(date);

        final DateTimeFormatter formatter =
                DateTimeFormat.forPattern(DATE_PATTERN).withLocale(Locale.forLanguageTag("cs-CZ"));
        if (dateTimeMatcher.matches()) {
            final LocalDate localDate;
            localDate = formatter.parseLocalDate(dateTimeMatcher.group(1) + " " + DateTime.now().getYear());
            item.setInsertionDate(localDate);
        }

        if (item.getInsertionDate() == null) {
            LOGGER.warn("Empty insertion date");
        }

    }

    private void setMainImageUrl(final Document document, final Item item) {

        final Element image = document.select(MAIN_IMAGE_SELECTOR).first();
        if (image != null) {
            item.setMainImageUrl(image.attr("content"));
        }

        if (StringUtils.isEmpty(item.getMainImageUrl())) {
            LOGGER.warn("Empty main image url");
        }

    }

    private void setOtherImagesUrl(final Document document, final Item item) {

        final Element imagesElement = document.select(OTHER_IMAGES_SELECTOR).first();
        if (imagesElement != null) {
            final String imageList = imagesElement.attr("data-img-collection");
            final Matcher matcher = OTHER_IMAGES_PATTERN.matcher(imageList);
            if (matcher.matches()) {
                final String images = matcher.group(1).replaceAll("\\\\", "").replaceAll("\"", "");
                item.getOtherImagesUrl().addAll(Arrays.asList(images.split(",")));
                if (!CollectionUtils.isEmpty(item.getOtherImagesUrl())) {
                    item.getOtherImagesUrl().remove(0);
                }
            }
        }

        if (CollectionUtils.isEmpty(item.getOtherImagesUrl())) {
            LOGGER.warn("Empty other image urls");
        }

    }

}
