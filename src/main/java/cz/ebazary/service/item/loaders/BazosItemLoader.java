package cz.ebazary.service.item.loaders;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.ItemLocality;
import cz.ebazary.model.item.Item;
import cz.ebazary.model.item.ItemCurrency;
import cz.ebazary.model.item.ItemPrice;
import cz.ebazary.utils.ItemLocalityUtil;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BazosItemLoader extends AbstractItemLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BazosItemLoader.class);

    private static final String DETAIL_SELECTOR = "table.inzeraty > tbody > tr";
    private static final String DATE_SELECTOR = "span.velikost10";
    private static final String MAIN_IMAGE_SELECTOR = "img#imgPrim";
    private static final String OTHER_IMAGES_SELECTOR = "input[id~=^hImg[2-9]\\d*";

    private static final String NEGOTIATED_PRICE = "Dohodou";
    private static final String IN_TEXT_PRICE = "V textu";
    private static final String OFFER = "Nabídněte";
    private static final String FREE_PRICE = "Zdarma";
    private static final String PRICE_DOES_NOT_MATTER = "Nerozhoduje";

    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final Pattern REGION_PATTERN = Pattern.compile("\\d{3} \\d{2} (.*)");
    private static final Pattern OFFER_PATTERN = Pattern.compile("-( TOP -)? Nabídka - \\[(\\d{1,2}.\\d{1,2}. \\d{4})\\]");

    private static final Map<Category, List<String>> CATEGORY_URLS = new HashMap<>();

    static {
        final List<String> carsUrls = new ArrayList<>();
        carsUrls.add("http://auto.bazos.cz");
        CATEGORY_URLS.put(Category.cars, carsUrls);

        final List<String> motorcyclesUrls = new ArrayList<>();
        motorcyclesUrls.add("http://motorky.bazos.cz");
        CATEGORY_URLS.put(Category.motorcycles, motorcyclesUrls);

        final List<String> machinesUrls = new ArrayList<>();
        machinesUrls.add("http://stroje.bazos.cz");
        CATEGORY_URLS.put(Category.machines, machinesUrls);

        final List<String> childrenUrls = new ArrayList<>();
        childrenUrls.add("http://deti.bazos.cz");
        CATEGORY_URLS.put(Category.children, childrenUrls);

        final List<String> homeUrls = new ArrayList<>();
        homeUrls.add("http://dum.bazos.cz");
        homeUrls.add("http://nabytek.bazos.cz");
        CATEGORY_URLS.put(Category.home, homeUrls);

        final List<String> animalsUrls = new ArrayList<>();
        animalsUrls.add("http://zvirata.bazos.cz");
        CATEGORY_URLS.put(Category.animals, animalsUrls);

        final List<String> sportUrls = new ArrayList<>();
        sportUrls.add("http://sport.bazos.cz");
        CATEGORY_URLS.put(Category.sport, sportUrls);

        final List<String> cultureUrls = new ArrayList<>();
        cultureUrls.add("http://hudba.bazos.cz");
        cultureUrls.add("http://vstupenky.bazos.cz");
        CATEGORY_URLS.put(Category.culture, cultureUrls);

        final List<String> booksUrls = new ArrayList<>();
        booksUrls.add("http://knihy.bazos.cz");
        CATEGORY_URLS.put(Category.books, booksUrls);

        final List<String> fashionUrls = new ArrayList<>();
        fashionUrls.add("http://obleceni.bazos.cz");
        CATEGORY_URLS.put(Category.fashion, fashionUrls);

        final List<String> computersUrls = new ArrayList<>();
        computersUrls.add("http://pc.bazos.cz/cd");
        computersUrls.add("http://pc.bazos.cz/graficka");
        computersUrls.add("http://pc.bazos.cz/hdd");
        computersUrls.add("http://pc.bazos.cz/playstation");
        computersUrls.add("http://pc.bazos.cz/volanty");
        computersUrls.add("http://pc.bazos.cz/hry");
        computersUrls.add("http://pc.bazos.cz/chladic");
        computersUrls.add("http://pc.bazos.cz/mys");
        computersUrls.add("http://pc.bazos.cz/kopirka");
        computersUrls.add("http://pc.bazos.cz/monitor");
        computersUrls.add("http://pc.bazos.cz/modem");
        computersUrls.add("http://pc.bazos.cz/notebook");
        computersUrls.add("http://pc.bazos.cz/pamet");
        computersUrls.add("http://pc.bazos.cz/pc");
        computersUrls.add("http://pc.bazos.cz/procesor");
        computersUrls.add("http://pc.bazos.cz/sit");
        computersUrls.add("http://pc.bazos.cz/scaner");
        computersUrls.add("http://pc.bazos.cz/case");
        computersUrls.add("http://pc.bazos.cz/software");
        computersUrls.add("http://pc.bazos.cz/spotrebni");
        computersUrls.add("http://pc.bazos.cz/tiskarna");
        computersUrls.add("http://pc.bazos.cz/wifi");
        computersUrls.add("http://pc.bazos.cz/motherboard");
        computersUrls.add("http://pc.bazos.cz/ups");
        computersUrls.add("http://pc.bazos.cz/sound");
        computersUrls.add("http://pc.bazos.cz/ostatni");
        CATEGORY_URLS.put(Category.computers, computersUrls);

        final List<String> mobileDevicesUrls = new ArrayList<>();
        mobileDevicesUrls.add("http://mobil.bazos.cz");
        mobileDevicesUrls.add("http://pc.bazos.cz/gps");
        mobileDevicesUrls.add("http://pc.bazos.cz/tablet");
        CATEGORY_URLS.put(Category.mobile_devices, mobileDevicesUrls);

        final List<String> appliancesUrls = new ArrayList<>();
        appliancesUrls.add("http://elektro.bazos.cz/digestore");
        appliancesUrls.add("http://elektro.bazos.cz/lednicky");
        appliancesUrls.add("http://elektro.bazos.cz/mikrovlnky");
        appliancesUrls.add("http://elektro.bazos.cz/mrazaky");
        appliancesUrls.add("http://elektro.bazos.cz/mycky");
        appliancesUrls.add("http://elektro.bazos.cz/pracky");
        appliancesUrls.add("http://elektro.bazos.cz/sporaky");
        appliancesUrls.add("http://elektro.bazos.cz/susicky");
        appliancesUrls.add("http://elektro.bazos.cz/ostatnibila");
        appliancesUrls.add("http://elektro.bazos.cz/epilatory");
        appliancesUrls.add("http://elektro.bazos.cz/feny");
        appliancesUrls.add("http://elektro.bazos.cz/holici");
        appliancesUrls.add("http://elektro.bazos.cz/kavovary");
        appliancesUrls.add("http://elektro.bazos.cz/nabijecky");
        appliancesUrls.add("http://elektro.bazos.cz/slehace");
        appliancesUrls.add("http://elektro.bazos.cz/lampy");
        appliancesUrls.add("http://elektro.bazos.cz/sicistroje");
        appliancesUrls.add("http://elektro.bazos.cz/vysavace");
        appliancesUrls.add("http://elektro.bazos.cz/vysilacky");
        appliancesUrls.add("http://elektro.bazos.cz/zvlhcovace");
        appliancesUrls.add("http://elektro.bazos.cz/zehlicky");
        appliancesUrls.add("http://elektro.bazos.cz/nastroje");
        appliancesUrls.add("http://foto.bazos.cz/karty");
        CATEGORY_URLS.put(Category.appliances, appliancesUrls);

        final List<String> audioVideoUrls = new ArrayList<>();
        audioVideoUrls.add("http://elektro.bazos.cz/autoradia");
        audioVideoUrls.add("http://elektro.bazos.cz/diskmany");
        audioVideoUrls.add("http://elektro.bazos.cz/kina");
        audioVideoUrls.add("http://elektro.bazos.cz/hifi");
        audioVideoUrls.add("http://pc.bazos.cz/mp3");
        audioVideoUrls.add("http://elektro.bazos.cz/repro");
        audioVideoUrls.add("http://elektro.bazos.cz/televizory");
        audioVideoUrls.add("http://elektro.bazos.cz/dvd");
        audioVideoUrls.add("http://foto.bazos.cz/videokamery");
        audioVideoUrls.add("http://elektro.bazos.cz/zesilovace");
        audioVideoUrls.add("http://elektro.bazos.cz/ostatniav");
        CATEGORY_URLS.put(Category.audio_video, audioVideoUrls);

        final List<String> photoUrls = new ArrayList<>();
        photoUrls.add("http://foto.bazos.cz/digitalni");
        photoUrls.add("http://foto.bazos.cz/zrcadlovky");
        photoUrls.add("http://foto.bazos.cz/kinofilm");
        photoUrls.add("http://foto.bazos.cz/baterie");
        photoUrls.add("http://foto.bazos.cz/blesky");
        photoUrls.add("http://foto.bazos.cz/brasny");
        photoUrls.add("http://foto.bazos.cz/kabely");
        photoUrls.add("http://foto.bazos.cz/filtry");
        photoUrls.add("http://foto.bazos.cz/nabijecky");
        photoUrls.add("http://foto.bazos.cz/objektivy");
        photoUrls.add("http://foto.bazos.cz/stativy");
        photoUrls.add("http://foto.bazos.cz/ostatni");
        CATEGORY_URLS.put(Category.photo, photoUrls);
    }

    @Override
    protected List<String> getCategoryUrls(final Category category) {

        final List<String> categoryUrls = CATEGORY_URLS.get(category);
        return categoryUrls == null ? new ArrayList<>() : categoryUrls;

    }

    @Override
    protected String getCategoryPageUrl(final String categoryUrl, final int page) {

        return page == 0 ? categoryUrl : categoryUrl + "/" + (page * 15) + "/";

    }

    @Override
    protected List<String> getItemUrls(final Document categoryPage) {

        final List<String> itemUrls = new ArrayList<>();

        String categoryPageUrl = categoryPage.location();
        if (categoryPageUrl.lastIndexOf('/') > 6) {
            categoryPageUrl = categoryPageUrl.substring(0, categoryPageUrl.indexOf('/', 7));
        }

        final Elements rows = categoryPage.select(DETAIL_SELECTOR);
        for (final Element row : rows) {
            final String location = row.select("div.popis").first().textNodes().get(1).text();
            if (!location.contains("Slovensko") && !location.contains("Zahraničí")) {
                final Elements type = row.select("span.velikost10");
                if (OFFER_PATTERN.matcher(type.text()).matches()) {
                    final String itemUrl = categoryPageUrl + row.select("td").get(0).select("a").attr("href");
                    itemUrls.add(itemUrl);
                }
            }
        }

        return itemUrls;

    }

    @Override
    protected Item getItem(final Document itemPage) {

        final Item item = new Item();
        item.setBazaarType(BazaarType.bazos);
        item.setUrl(itemPage.location());

        setPrice(itemPage, item);
        setDescription(itemPage, item);
        setPhone(itemPage, item);
        setLocality(itemPage, item);
        setInsertionDate(itemPage, item);
        setMainImageUrl(itemPage, item);
        setOtherImagesUrl(itemPage, item);

        return item;

    }

    private void setPrice(final Document document, final Item item) {

        final ItemPrice itemPrice = new ItemPrice();

        final Elements tables = document.select("table");
        final Element price = tables.get(6).select("tbody > tr").get(2).select("td").get(1);

        if (NEGOTIATED_PRICE.equals(price.text()) || OFFER.equals(price.text())) {
            itemPrice.setNegotiatedPrice(true);
        } else if (IN_TEXT_PRICE.equals(price.text())) {
            itemPrice.setPriceInDescription(true);
        } else if (FREE_PRICE.equals(price.text())) {
            itemPrice.setPrice(BigDecimal.ZERO);
        } else if (PRICE_DOES_NOT_MATTER.equals(price.text())) {
            itemPrice.setNegotiatedPrice(true);
        } else {
            final String priceValue = StringUtils.trimAllWhitespace(price.text().substring(0, price.text().lastIndexOf(" ")));
            if (isNumeric(priceValue)) {
                itemPrice.setPrice(new BigDecimal(priceValue));

                final String currency = price.text().substring(price.text().lastIndexOf(" ") + 1, price.text().length());
                itemPrice.setCurrency(
                        ItemCurrency
                                .findByName(currency)
                                .orElseThrow(() -> new IllegalArgumentException("Currency " + currency + " not recognized"))
                );

            }
        }

        if (itemPrice.getPrice() == null && !itemPrice.isNegotiatedPrice() && !itemPrice.isPriceInDescription()) {
            throw new IllegalStateException(price.text());
        }

        item.setItemPrice(itemPrice);

    }

    private void setDescription(final Document document, final Item item) {

        final Element description = document.select("table").get(4).select("tbody > tr").get(1);
        item.setDescription(description.text());

    }

    private void setPhone(final Document document, final Item item) {

        final Element phone = document.select("table").get(6).select("tbody > tr").get(0).select("td").get(1).select("a").get(1);
        item.setPhoneNumber(StringUtils.isEmpty(phone.text()) ? null : phone.text());

    }

    private void setLocality(final Document document, final Item item) {

        final Element address = document.select("table").get(6).select("tbody > tr").get(1).select("td").get(2).select("a").get(0);
        final Matcher matcher = REGION_PATTERN.matcher(address.text());

        final String localityString;
        if (matcher.matches()) {
            localityString = matcher.group(1);
        } else {
            localityString = address.text();
        }

        final ItemLocality itemLocality =
                ItemLocalityUtil
                        .getItemLocality(localityString)
                        .orElseThrow(() -> new IllegalStateException("Unsupported location " + localityString));

        item.setItemLocality(itemLocality);


    }

    private void setInsertionDate(final Document document, final Item item) {

        final Elements date = document.select(DATE_SELECTOR);
        final Matcher matcher = OFFER_PATTERN.matcher(date.text());
        if (matcher.matches()) {
            final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_PATTERN);
            final LocalDate localDate = formatter.parseLocalDate(StringUtils.trimAllWhitespace(matcher.group(2)));
            item.setInsertionDate(localDate);
        }

    }

    private void setMainImageUrl(final Document document, final Item item) {

        final Elements image = document.select(MAIN_IMAGE_SELECTOR);
        if(!image.isEmpty()) {
            item.setMainImageUrl(image.attr("src"));
        }

    }

    private void setOtherImagesUrl(final Document document, final Item item) {

        final Elements images = document.select(OTHER_IMAGES_SELECTOR);
        for (Element img : images) {
            item.getOtherImagesUrl().add(img.attr("value"));
        }


    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+((\\.|,)\\d+)?");
    }

}
