package cz.ebazary.model.result;

import cz.ebazary.model.bazaar.BazaarType;
import lombok.Data;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Result {
    private BazaarType bazaarType;
    private String url;
    private LocalDate insertionDate;
    private String description;
    private BigDecimal price;
    private boolean negotiatedPrice;
    private String address;
    private String mainImageUrl;
    private List<String> otherImagesUrl;
    private String phoneNumber;
    private String email;
    private ResultCurrency currency;
}
