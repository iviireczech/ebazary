package cz.ebazary.service.item.loaders;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.model.bazaar.locality.ItemLocality;
import cz.ebazary.model.item.Item;
import cz.ebazary.model.item.ItemCurrency;
import cz.ebazary.model.item.ItemPrice;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BazarItemLoader extends AbstractItemLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BazarItemLoader.class);

    private static final String DETAIL_SELECTOR = "div.text";
    private static final String DATE_SELECTOR = "span.velikost10";
    private static final String MAIN_IMAGE_SELECTOR = "img#imgPrim";
    private static final String OTHER_IMAGES_SELECTOR = "input[id~=^hImg[2-9]\\d*";

    private static final String NEGOTIATED_PRICE = "Dohodou";
    private static final String IN_TEXT_PRICE = "V textu";
    private static final String OFFER = "Nabídněte";
    private static final String FREE_PRICE = "Zdarma";
    private static final String PRICE_NOT_LISTED = "neuvedena";

    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final Pattern REGION_PATTERN = Pattern.compile("\\d{3} \\d{2} (.*)");
    private static final Pattern OFFER_PATTERN = Pattern.compile("-( TOP -)? Nabídka - \\[(\\d{1,2}.\\d{2}. \\d{4})\\]");

    private static final String NO_BREAK_SPACE = "\u00a0";

    private static final Map<Category, List<String>> CATEGORY_URLS = new HashMap<>();

    static {
        final List<String> carsUrls = new ArrayList<>();
        carsUrls.add("http://auta.bazar.cz/osobni-auta");
        carsUrls.add("http://auta.bazar.cz/uzitkova-auta");
        carsUrls.add("http://auta.bazar.cz/autodily/");
        CATEGORY_URLS.put(Category.cars, carsUrls);

        final List<String> motorcyclesUrls = new ArrayList<>();
        motorcyclesUrls.add("http://auta.bazar.cz/silnicni-motorky");
        CATEGORY_URLS.put(Category.motorcycles, motorcyclesUrls);

        final List<String> machinesUrls = new ArrayList<>();
        machinesUrls.add("http://stroje.bazar.cz");
        CATEGORY_URLS.put(Category.machines, machinesUrls);

        final List<String> childrenUrls = new ArrayList<>();
        childrenUrls.add("http://detske.bazar.cz");
        CATEGORY_URLS.put(Category.children, childrenUrls);

        final List<String> homeUrls = new ArrayList<>();
        homeUrls.add("http://stavba-dum-zahrada.bazar.cz");
        homeUrls.add("http://kancelar.bazar.cz");
        homeUrls.add("http://nabytek.bazar.cz");
        CATEGORY_URLS.put(Category.home, homeUrls);

        final List<String> animalsUrls = new ArrayList<>();
        animalsUrls.add("http://chovatelstvi-zvirata.bazar.cz");
        CATEGORY_URLS.put(Category.animals, animalsUrls);

        final List<String> sportUrls = new ArrayList<>();
        sportUrls.add("http://sport.bazar.cz");
        sportUrls.add("http://hry-hobby.bazar.cz/zabava");
        sportUrls.add("http://hry-hobby.bazar.cz/hobby");
        sportUrls.add("http://hry-hobby.bazar.cz/hobby-rybarstvi-myslivost");
        sportUrls.add("http://hry-hobby.bazar.cz/hobby-airsoft-paintball");
        sportUrls.add("http://outdoor-turistika.bazar.cz");
        CATEGORY_URLS.put(Category.sport, sportUrls);

        final List<String> cultureUrls = new ArrayList<>();
        cultureUrls.add("http://filmy-hudba.bazar.cz");
        cultureUrls.add("http://hudebni-nastroje.bazar.cz");
        CATEGORY_URLS.put(Category.culture, cultureUrls);

        final List<String> booksUrls = new ArrayList<>();
        booksUrls.add("http://antikvariat.bazar.cz");
        CATEGORY_URLS.put(Category.books, booksUrls);

        final List<String> fashionUrls = new ArrayList<>();
        fashionUrls.add("http://obleceni-boty-doplnky.bazar.cz");
        fashionUrls.add("http://obleceni-boty-doplnky.bazar.cz/boty");
        CATEGORY_URLS.put(Category.fashion, fashionUrls);

        final List<String> computersUrls = new ArrayList<>();
        computersUrls.add("http://pc.bazar.cz/stolni-pocitace");
        computersUrls.add("http://pc.bazar.cz/notebooky");
        computersUrls.add("http://pc.bazar.cz/ostatni-pc");
        computersUrls.add("http://pc.bazar.cz/tiskarny-scannery");
        computersUrls.add("http://pc.bazar.cz/pocitace-prislusenstvi");
        computersUrls.add("http://pc.bazar.cz/dily-komponenty");
        computersUrls.add("http://pc.bazar.cz/media");
        computersUrls.add("http://pc.bazar.cz/software");
        computersUrls.add("http://pc.bazar.cz/pocitace-pametove-karty");
        CATEGORY_URLS.put(Category.computers, computersUrls);

        final List<String> mobileDevicesUrls = new ArrayList<>();
        mobileDevicesUrls.add("http://mobil.bazar.cz");
        mobileDevicesUrls.add("http://pc.bazar.cz/pda-mda");
        mobileDevicesUrls.add("http://pc.bazar.cz/tablety-ebook-ctecky");
        CATEGORY_URLS.put(Category.mobile_devices, mobileDevicesUrls);

        final List<String> appliancesUrls = new ArrayList<>();
        appliancesUrls.add("http://bila-technika.bazar.cz");
        CATEGORY_URLS.put(Category.appliances, appliancesUrls);

        final List<String> audioVideoUrls = new ArrayList<>();
        audioVideoUrls.add("http://elektro.bazar.cz/audio");
        audioVideoUrls.add("http://elektro.bazar.cz/video");
        CATEGORY_URLS.put(Category.audio_video, audioVideoUrls);

        final List<String> photoUrls = new ArrayList<>();
        photoUrls.add("http://foto.bazar.cz/fotoaparaty");
        photoUrls.add("http://foto.bazar.cz/foto-prislusenstvi");
        photoUrls.add("http://foto.bazar.cz/pametove-karty");
        CATEGORY_URLS.put(Category.photo, photoUrls);
    }

    @Override
    protected List<String> getCategoryUrls(final Category category) {

        return CATEGORY_URLS.get(category);

    }

    @Override
    protected String getCategoryPageUrl(final String categoryUrl, final int page) {

        return categoryUrl + "/" + (page + 1) + "/";

    }

    @Override
    protected List<String> getItemUrls(final Document categoryPage) {

        final List<String> itemUrls = new ArrayList<>();

        final Elements rows = categoryPage.select(DETAIL_SELECTOR);
        for (Element row : rows) {
            final String type = row.select("div.kat").first().textNodes().get(0).text();
            if (", Nabídka ".equals(type)) {
                final String itemUrl = row.select("h2.nulink > a").attr("href");
                itemUrls.add(itemUrl);
            }
        }

        return itemUrls;

    }

    @Override
    protected Item getItem(final Document itemPage) {

        final Item item = new Item();
        item.setBazaarType(BazaarType.bazar);
        item.setUrl(itemPage.location());

        setPrice(itemPage, item);
        setDescription(itemPage, item);
        setPhone(itemPage, item);
        setEmail(itemPage, item);
        setLocality(itemPage, item);
        setInsertionDate(itemPage, item);
        setMainImageUrl(itemPage, item);
        setOtherImagesUrl(itemPage, item);

        return item;

    }

    private void setPrice(final Document document, final Item item) {

        final ItemPrice itemPrice = new ItemPrice();

        final String currency = document.select("span.kc").text();
        if (PRICE_NOT_LISTED.equals(currency)) {
            itemPrice.setNegotiatedPrice(true);
        } else {
            final String price = document.select("span.cena").get(0).textNodes().get(0).text();

            final String priceValue = StringUtils.trimAllWhitespace(price.replace(NO_BREAK_SPACE, ""));

            if (isNumeric(priceValue)) {
                itemPrice.setPrice(new BigDecimal(priceValue));

                itemPrice.setCurrency(
                        ItemCurrency
                                .findByName(currency)
                                .orElseThrow(() -> new IllegalArgumentException("Currency " + currency + " not recognized"))
                );

            }

        }

        if (itemPrice.getPrice() == null && !itemPrice.isNegotiatedPrice() && !itemPrice.isPriceInDescription()) {
            throw new IllegalStateException(currency);
        }

        item.setItemPrice(itemPrice);

    }

    private void setDescription(final Document document, final Item item) {

        final Elements description = document.select("div.text");
        item.setDescription(description.text());

        if (StringUtils.isEmpty(item.getDescription())) {
            LOGGER.warn("Empty description");
        }

    }

    private void setPhone(final Document document, final Item item) {

        final Elements phone = document.select("span.telefon");
        item.setPhoneNumber(StringUtils.isEmpty(phone.text()) ? null : phone.text());

        if (StringUtils.isEmpty(item.getPhoneNumber())) {
            LOGGER.warn("Empty phone number");
        }

    }

    private void setEmail(final Document document, final Item item) {

        final Elements email = document.select("span.parseMail");
        if (!email.isEmpty()) {
            item.setEmail(email.first().textNodes().get(0).text().replace("#", ".").replace("|", "@"));
        }

        if (StringUtils.isEmpty(item.getEmail())) {
            LOGGER.warn("Empty email");
        }

    }

    private void setLocality(final Document document, final Item item) {

        final Elements tds = document.select("table#atributy > tbody > tr > td");
        for (int i = 0; i < tds.size(); i++) {
            final Element td = tds.get(i);
            if ("Region:".equals(td.text())) {
                final Pattern pattern = Pattern.compile(".* \\\\ (.*) \\\\ .*");
                final String address = tds.get(i + 1).select("span").attr("title");
                final Matcher m = pattern.matcher(address);
                if (m.matches()) {
                    final Matcher matcher = REGION_PATTERN.matcher(m.group(1));

                    final District district;
                    if (matcher.matches()) {
                        district =
                                District
                                        .findByName(matcher.group(1))
                                        .orElseThrow(() -> new IllegalArgumentException("Disctrict " + matcher.group(1) + " not recognized"));
                    } else {
                        district =
                                District
                                        .findByName(m.group(1))
                                        .orElseThrow(() -> new IllegalArgumentException("Disctrict " + m.group(1) + " not recognized"));
                    }

                    final ItemLocality itemLocality = new ItemLocality();
                    itemLocality.setDistrict(district);
                    item.setItemLocality(itemLocality);
                    break;
                }
            }
        }

        if (item.getItemLocality().getDistrict() == null && item.getItemLocality().getRegion() == null) {
            throw new IllegalStateException("Empty location");
        }


    }

    private void setInsertionDate(final Document document, final Item item) {

        final Elements tds = document.select("table#atributy > tbody > tr > td");
        for (int i = 0; i < tds.size(); i++) {
            final Element td = tds.get(i);
            if ("Aktualizováno:".equals(td.text())) {
                final String date = tds.get(i + 1).text();
                final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_PATTERN);
                final LocalDate localDate = formatter.parseLocalDate(StringUtils.trimAllWhitespace(date));
                item.setInsertionDate(localDate);
            }
        }

        if (item.getInsertionDate() == null) {
            LOGGER.warn("Empty insertion date");
        }

    }

    private void setMainImageUrl(final Document document, final Item item) {

        final Elements image = document.select("div#gallery > table");
        if(!image.isEmpty()) {
            item.setMainImageUrl("http://auta.bazar.cz" + image.select("tbody > tr").get(0).select("td").get(0).select("a").attr("href"));
        }

        if (StringUtils.isEmpty(item.getMainImageUrl())) {
            LOGGER.warn("Empty main image url");
        }

    }

    private void setOtherImagesUrl(final Document document, final Item item) {

        final Elements images = document.select("table.foto > tbody > tr > td > a");
        for (Element img : images) {
            item.getOtherImagesUrl().add("http://auta.bazar.cz" + img.attr("href"));
        }

        if (CollectionUtils.isEmpty(item.getOtherImagesUrl())) {
            LOGGER.warn("Empty other image urls");
        }

    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+((\\.|,)\\d+)?");
    }

}