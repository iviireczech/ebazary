package cz.ebazary.model.item;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.locality.Locality;
import lombok.Data;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Item {
    private BazaarType bazaarType;
    private String url;
    private LocalDate insertionDate;
    private String description;
    private BigDecimal price;
    private ItemCurrency currency;
    private boolean negotiatedPrice;
    private boolean priceInDescription;
    private String mainImageUrl;
    private List<String> otherImagesUrl;
    private Locality locality;
    private String phoneNumber;
    private String email;

    public Item() {
        otherImagesUrl = new ArrayList<>();
    }

}
