package cz.ebazary.service.item.loaders;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.model.bazaar.locality.Locality;
import cz.ebazary.model.item.Item;
import cz.ebazary.model.item.ItemCurrency;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SBazarItemLoader implements Loadable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SBazarItemLoader.class);

    private static final String ROOT_URL = "http://www.sbazar.cz";
    private static final String DETAIL_URL = "/cela-cr/cena-neomezena/nejnovejsi/";

    private static final String DETAIL_SELECTOR = "a[href].mrEggPart";
    private static final String PRICE_SELECTOR = "span[itemprop=price]";
    private static final String CURRENCY_SELECTOR = "span.currency";
    private static final String DESCRIPTION_SELECTOR = "div.description";
    private static final String PHONE_SELECTOR = "span[itemprop=telephone]";
    private static final String ADDRESS_SELECTOR = "span[itemprop=addressRegion]";
    private static final String DATE_SELECTOR = "span.date";
    private static final String MAIN_IMAGE_SELECTOR = "img[itemprop=image]";
    private static final String OTHER_IMAGES_SELECTOR = "div.fotka > img";

    private static final String NEGOTIATED_PRICE = "Dohodou";
    private static final String TO_LEAVE = "Přenechám";
    private static final String FREE_PRICE = "Zdarma";

    private static final String NO_BREAK_SPACE = "\u00a0";

    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final Pattern REGION_PATTERN = Pattern.compile("(.*), okres (.*)");

    private static final Map<Category, List<String>> CATEGORY_URLS = new HashMap<>();

    static {
        final List<String> carsUrls = new ArrayList<>();
        carsUrls.add("http://www.sbazar.cz/210-bourana-auta");
        carsUrls.add("http://www.sbazar.cz/4-nahradni-dily-kola-prislusenstvi");
        carsUrls.add("http://www.sbazar.cz/175-do-35-t");
        carsUrls.add("http://www.sbazar.cz/177-kamiony-tahace");
        carsUrls.add("http://www.sbazar.cz/176-nad-35-t");
        carsUrls.add("http://www.sbazar.cz/179-nahradni-dily-prislusenstvi");
        carsUrls.add("http://www.sbazar.cz/178-navesy-privesy");
        carsUrls.add("http://www.sbazar.cz/151-offroad");
        carsUrls.add("http://www.sbazar.cz/170-osobni-auta");
        carsUrls.add("http://www.sbazar.cz/76-ostatni-auta");
        carsUrls.add("http://www.sbazar.cz/19-tuning");
        carsUrls.add("http://www.sbazar.cz/190-veterani");
        carsUrls.add("http://www.sbazar.cz/262-ctyrkolky");
        CATEGORY_URLS.put(Category.cars, carsUrls);

        final List<String> motorcyclesUrls = new ArrayList<>();
        motorcyclesUrls.add("http://www.sbazar.cz/85-motocykly");
        CATEGORY_URLS.put(Category.motorcycles, motorcyclesUrls);

        final List<String> machinesUrls = new ArrayList<>();
        machinesUrls.add("http://www.sbazar.cz/282-manipulacni-technika");
        machinesUrls.add("http://www.sbazar.cz/280-stavebni-zemni-stroje");
        machinesUrls.add("http://www.sbazar.cz/230-drevoobrabeci-stroje");
        machinesUrls.add("http://www.sbazar.cz/231-kovoobrabeci-stroje");
        machinesUrls.add("http://www.sbazar.cz/229-zemedelska-lesni-technika");
        CATEGORY_URLS.put(Category.machines, machinesUrls);

        final List<String> childrenUrls = new ArrayList<>();
        childrenUrls.add("http://www.sbazar.cz/29-detsky-bazar");
        childrenUrls.add("http://www.sbazar.cz/6-hry-hracky");
        CATEGORY_URLS.put(Category.children, childrenUrls);

        final List<String> homeUrls = new ArrayList<>();
        homeUrls.add("http://www.sbazar.cz/822-bazeny-sauny-virivky-solaria");
        homeUrls.add("http://www.sbazar.cz/23-bytove-doplnky");
        homeUrls.add("http://www.sbazar.cz/290-ikea-bazar");
        homeUrls.add("http://www.sbazar.cz/20-nabytek");
        homeUrls.add("http://www.sbazar.cz/818-nadobi");
        homeUrls.add("http://www.sbazar.cz/74-ostatni-veci-do-domacnosti");
        homeUrls.add("http://www.sbazar.cz/821-podlahy-koberce");
        homeUrls.add("http://www.sbazar.cz/22-stavebnictvi-naradi");
        homeUrls.add("http://www.sbazar.cz/823-vytapeni-ohrev-chlazeni");
        homeUrls.add("http://www.sbazar.cz/21-zahrada");
        CATEGORY_URLS.put(Category.home, homeUrls);

        final List<String> animalsUrls = new ArrayList<>();
        animalsUrls.add("http://www.sbazar.cz/90-zvirata");
        CATEGORY_URLS.put(Category.animals, animalsUrls);

        final List<String> sportUrls = new ArrayList<>();
        sportUrls.add("http://www.sbazar.cz/27-sport");
        CATEGORY_URLS.put(Category.sport, sportUrls);

        final List<String> cultureUrls = new ArrayList<>();
        cultureUrls.add("http://www.sbazar.cz/317-blu-ray-dvd-filmy");
        cultureUrls.add("http://www.sbazar.cz/316-hudba-cd");
        cultureUrls.add("http://www.sbazar.cz/318-hudebni-nastroje-prislusenstvi");
        cultureUrls.add("http://www.sbazar.cz/836-vouchery-kupony-poukazky");
        cultureUrls.add("http://www.sbazar.cz/321-vstupenky");
        CATEGORY_URLS.put(Category.culture, cultureUrls);

        final List<String> booksUrls = new ArrayList<>();
        booksUrls.add("http://www.sbazar.cz/31-knihy-literatura");
        CATEGORY_URLS.put(Category.books, booksUrls);

        final List<String> fashionUrls = new ArrayList<>();
        fashionUrls.add("http://www.sbazar.cz/15-obleceni-obuv-doplnky");
        CATEGORY_URLS.put(Category.fashion, fashionUrls);

        final List<String> computersUrls = new ArrayList<>();
        computersUrls.add("http://www.sbazar.cz/816-herni-konzole");
        computersUrls.add("http://www.sbazar.cz/584-pametove-karty");
        computersUrls.add("http://www.sbazar.cz/32-pocitace");
        CATEGORY_URLS.put(Category.computers, computersUrls);

        final List<String> mobileDevicesUrls = new ArrayList<>();
        mobileDevicesUrls.add("http://www.sbazar.cz/586-gps-navigace");
        mobileDevicesUrls.add("http://www.sbazar.cz/94-mobil-bazar");
        mobileDevicesUrls.add("http://www.sbazar.cz/815-tablety-ctecky-knih");
        CATEGORY_URLS.put(Category.mobile_devices, mobileDevicesUrls);

        final List<String> appliancesUrls = new ArrayList<>();
        appliancesUrls.add("http://www.sbazar.cz/73-ostatni-elektro");
        appliancesUrls.add("http://www.sbazar.cz/597-satelity-satelitni-komplety");
        appliancesUrls.add("http://www.sbazar.cz/577-televize");
        appliancesUrls.add("http://www.sbazar.cz/11-domaci-spotrebice");
        CATEGORY_URLS.put(Category.appliances, appliancesUrls);

        final List<String> audioVideoUrls = new ArrayList<>();
        audioVideoUrls.add("http://www.sbazar.cz/50-audio-video");
        CATEGORY_URLS.put(Category.audio_video, audioVideoUrls);

        final List<String> photoUrls = new ArrayList<>();
        photoUrls.add("http://www.sbazar.cz/49-foto-bazar");
        CATEGORY_URLS.put(Category.photo, photoUrls);
    }

    @Override
    public List<Item> loadItems(final LocalDate from) {
        final List<Item> items = new ArrayList<>();
        try {

            int i = 0;
            LocalDate current = LocalDate.now();
            while (!current.isBefore(from)){
                for (Map.Entry<Category, List<String>> categoryUrlsEntry : CATEGORY_URLS.entrySet()) {
                    for (String mainUrl : categoryUrlsEntry.getValue()) {
                        final Document main = Jsoup.connect(mainUrl + DETAIL_URL + i++).get();
                        final Elements links = main.select(DETAIL_SELECTOR);
                        for (Element link : links) {
                            final String itemUrl = ROOT_URL + link.attr("href");
                            final Document detail = Jsoup.connect(itemUrl).get();

                            final Item item = new Item();
                            item.setBazaarType(BazaarType.sbazar);
                            item.setUrl(itemUrl);

                            setPrice(detail, item);
                            setDescription(detail, item);
                            setPhone(detail, item);
                            setLocality(detail, item);
                            setInsertionDate(detail, item);
                            setMainImageUrl(detail, item);
                            setOtherImagesUrl(detail, item);

                            LOGGER.debug(item.toString());

                            items.add(item);
                        }
                    }
                }

            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return items;

    }

    private void setPrice(final Document document, final Item item) {

        final Elements price = document.select(PRICE_SELECTOR);

        final String priceString = price.first().textNodes().get(0).text().replace(NO_BREAK_SPACE,"");
        if (isNumeric(priceString)) {
            item.setPrice(new BigDecimal(priceString));

            final Elements currency = document.select(CURRENCY_SELECTOR);
            item.setCurrency(
                    ItemCurrency
                            .findByName(currency.text())
                            .orElseThrow(() -> new IllegalArgumentException("Currency " + currency + " not recognized"))
            );

        } else if (NEGOTIATED_PRICE.equals(priceString)) {
            item.setNegotiatedPrice(true);
        } else if (TO_LEAVE.equals(priceString) || FREE_PRICE.equals(priceString)) {
            item.setPrice(BigDecimal.ZERO);
        }

    }

    private void setDescription(final Document document, final Item item) {

        final Elements description = document.select(DESCRIPTION_SELECTOR);
        item.setDescription(description.text());

    }

    private void setPhone(final Document document, final Item item) {

        final Elements phone = document.select(PHONE_SELECTOR);
        item.setPhoneNumber(StringUtils.isEmpty(phone.text()) ? null : phone.text());

    }

    private void setLocality(final Document document, final Item item) {

        final Elements address = document.select(ADDRESS_SELECTOR);
        final Matcher matcher = REGION_PATTERN.matcher(address.text());

        final District district;
        if (matcher.matches()) {
            district =
                    District
                            .findByName(matcher.group(2))
                            .orElseThrow(() -> new IllegalArgumentException("Disctrict " + matcher.group(2) + " not recognized"));
        } else {
            district =
                    District
                            .findByName(address.text())
                            .orElseThrow(() -> new IllegalArgumentException("Disctrict " + address.text() + " not recognized"));
        }

        final Locality locality = new Locality();
        locality.setDistrict(district);
        item.setLocality(locality);

    }

    private void setInsertionDate(final Document document, final Item item) {

        final Elements date = document.select(DATE_SELECTOR);
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_PATTERN);
        final LocalDate localDate = formatter.parseLocalDate(date.text());
        item.setInsertionDate(localDate);

    }

    private void setMainImageUrl(final Document document, final Item item) {

        final Elements image = document.select(MAIN_IMAGE_SELECTOR);
        if(!image.isEmpty()) {
            item.setMainImageUrl(image.attr("src").substring(2));
        }

    }

    private void setOtherImagesUrl(final Document document, final Item item) {

        final Elements images = document.select(OTHER_IMAGES_SELECTOR);
        for (Element img : images) {
            item.getOtherImagesUrl().add(img.attr("src").substring(2));
        }

    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+((\\.|,)\\d+)?");
    }

}
