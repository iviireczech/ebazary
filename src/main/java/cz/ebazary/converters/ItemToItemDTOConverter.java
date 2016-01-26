package cz.ebazary.converters;

import cz.ebazary.dto.ItemDTO;
import cz.ebazary.model.bazaar.locality.ItemLocality;
import cz.ebazary.model.item.Item;
import cz.ebazary.model.item.ItemPrice;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ItemToItemDTOConverter {

    private static final int MAX_DESCRIPTION_LENGTH = 297;

    private static final String NEGOTIATED_PRICE = "Dohodou";
    private static final String IN_DESCRIPTION_PRICE = "V textu";

    private static final String MISSING_IMAGE_URL = "images/image_missing.jpg";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");

    private ItemToItemDTOConverter() {
    }

    public static List<ItemDTO> convert(final List<Item> items) {

        return items
                .stream()
                .map(item -> {

                    final ItemDTO itemDTO = new ItemDTO();
                    itemDTO.setBazaarName(item.getBazaarType().getName());
                    itemDTO.setCategory(item.getCategory().name());
                    itemDTO.setUrl(item.getUrl());
                    itemDTO.setInsertionDate(DATE_TIME_FORMATTER.print(item.getInsertionDate()));
                    itemDTO.setDescription(shrinkItemDescription(item.getDescription()));
                    itemDTO.setItemPrice(getItemPriceAsString(item.getItemPrice()));
                    itemDTO.setMainImageUrl(fixMissingMainImageUrl(item.getMainImageUrl()));
                    itemDTO.getOtherImagesUrl().addAll(item.getOtherImagesUrl());
                    itemDTO.setItemLocality(getItemLocalityAsString(item.getItemLocality()));
                    itemDTO.setPhoneNumber(item.getPhoneNumber());
                    itemDTO.setEmail(item.getEmail());

                    return itemDTO;

                })
                .collect(Collectors.toList());

    }

    private static String shrinkItemDescription(final String description) {

        if (!StringUtils.isEmpty(description) && description.length() > MAX_DESCRIPTION_LENGTH) {
            return description.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
        } else {
            return description;
        }

    }

    private static String getItemPriceAsString(final ItemPrice itemPrice) {

        if (itemPrice.isNegotiatedPrice()) return NEGOTIATED_PRICE;
        if (itemPrice.isPriceInDescription()) return IN_DESCRIPTION_PRICE;

        return NumberFormat
                .getNumberInstance(Locale.forLanguageTag("cs-CZ"))
                .format(itemPrice.getPrice()) + " " + itemPrice.getCurrency().getName();

    }

    private static String fixMissingMainImageUrl(final String mainImageUrl) {

        return StringUtils.isEmpty(mainImageUrl) ? MISSING_IMAGE_URL : mainImageUrl;

    }

    private static String getItemLocalityAsString(final ItemLocality itemLocality) {

        return itemLocality.getDistrict() != null ? itemLocality.getDistrict().getName() : itemLocality.getRegion().getName();

    }


}
