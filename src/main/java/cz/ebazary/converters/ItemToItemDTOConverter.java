package cz.ebazary.converters;

import cz.ebazary.dto.ItemDTO;
import cz.ebazary.model.item.Item;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ItemToItemDTOConverter {

    private static final int MAX_DESCRIPTION_LENGTH = 297;

    private static final String NEGOTIATED_PRICE = "Dohodou";
    private static final String IN_DESCRIPTION_PRICE = "V textu";

    private static final String MISSING_IMAGE_URL = "images/image_missing.jpg";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private ItemToItemDTOConverter() {
    }

    public static List<ItemDTO> convert(final Iterable<Item> items) {

        final List<ItemDTO> itemDTOs = new ArrayList<>();
        for (final Item item : items) {
            final ItemDTO itemDTO = new ItemDTO();
            itemDTO.setBazaarName(item.getBazaarType());
            itemDTO.setCategory(item.getCategory());
            itemDTO.setUrl(item.getUrl());
            itemDTO.setInsertionDate(SIMPLE_DATE_FORMAT.format(item.getInsertionDate()));
            itemDTO.setDescription(shrinkItemDescription(item.getDescription()));
            itemDTO.setItemPrice(getItemPriceAsString(item));
            itemDTO.setMainImageUrl(fixMissingMainImageUrl(item.getMainImageUrl()));
            itemDTO.getOtherImagesUrl().addAll(item.getOtherImagesUrl());
            itemDTO.setItemLocality(getItemLocalityAsString(item));
            itemDTO.setPhoneNumber(item.getPhoneNumber());
            itemDTO.setEmail(item.getEmail());

            itemDTOs.add(itemDTO);
        }

        return itemDTOs;

    }

    private static String shrinkItemDescription(final String description) {

        if (!StringUtils.isEmpty(description) && description.length() > MAX_DESCRIPTION_LENGTH) {
            return description.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
        } else {
            return description;
        }

    }

    private static String getItemPriceAsString(final Item item) {

        if (item.isNegotiatedPrice()) return NEGOTIATED_PRICE;
        if (item.isPriceInDescription()) return IN_DESCRIPTION_PRICE;

        return NumberFormat
                .getNumberInstance(Locale.forLanguageTag("cs-CZ"))
                .format(item.getPrice()) + " " + item.getCurrency();

    }

    private static String fixMissingMainImageUrl(final String mainImageUrl) {

        return StringUtils.isEmpty(mainImageUrl) ? MISSING_IMAGE_URL : mainImageUrl;

    }

    private static String getItemLocalityAsString(final Item item) {

        return item.getDistrict() != null ? item.getDistrict() : item.getRegion();

    }


}
