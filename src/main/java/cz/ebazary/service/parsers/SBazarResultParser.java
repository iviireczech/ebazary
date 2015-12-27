package cz.ebazary.service.parsers;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.result.Result;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SBazarResultParser implements ResultParser {

    @Override
    public List<Result> parseResults() {
        final List<Result> results = new ArrayList<>();
        Document doc;
        try {

            int i = 0;
            while (true) {
                doc = Jsoup.connect("http://www.sbazar.cz/0-vsechny-kategorie/cela-cr/cena-neomezena/nejnovejsi/" + i++).get();
                Elements links = doc.select("a[href].mrEggPart");
                for (Element link : links) {
                    final Document detail = Jsoup.connect("http://www.sbazar.cz" + link.attr("href")).get();
                    final Result result = new Result();
                    result.setBazaarType(BazaarType.sbazar);
                    result.setUrl("http://www.sbazar.cz" + link.attr("href"));
                    Elements price = detail.select("span[itemprop=price]");
                    final String priceString = price.first().textNodes().get(0).text().replace("\u00a0","");
                    if (isNumeric(priceString)) {
                        result.setPrice(new BigDecimal(priceString));
                    } else if ("Dohodou".equals(priceString)) {
                        result.setNegotiatedPrice(true);
                    } else if ("Přenechám".equals(priceString) || "Zdarma".equals(priceString)) {
                        result.setPrice(BigDecimal.ZERO);
                    }
                    Elements description = detail.select("div.description");
                    result.setDescription(description.text());
                    Elements phone = detail.select("span[itemprop=telephone]");
                    result.setPhoneNumber(StringUtils.isEmpty(phone.text()) ? null : phone.text());
                    Elements address = detail.select("span[itemprop=addressRegion]");
                    result.setAddress(address.text() );
                    Elements date = detail.select("span.date");
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
                    LocalDate localDate = formatter.parseLocalDate(date.text());
                    result.setInsertionDate(localDate);
                    Elements image = detail.select("img[itemprop=image]");
                    if(!image.isEmpty()) {
                        result.setMainImageUrl(image.attr("src").substring(2));
                    }
                    result.setOtherImagesUrl(new ArrayList<>());
                    Elements images = detail.select("div.fotka > img");
                    for (Element img : images) {
                        result.getOtherImagesUrl().add(img.attr("src").substring(2));
                    }

                    results.add(result);

                }

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return results;

    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+((\\.|,)\\d+)?");  //match a number with optional '-' and decimal.
    }

}
