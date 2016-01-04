package cz.ebazary.model.item;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemPrice {

    private BigDecimal price;
    private ItemCurrency currency;
    private boolean negotiatedPrice;
    private boolean priceInDescription;

}
