package cz.ebazary.model.item;

import java.util.Arrays;
import java.util.Optional;

public enum ItemCurrency {

    czk("Kč");

    private final String name;

    ItemCurrency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<ItemCurrency> findByName(final String name) {

        return Arrays
                .stream(ItemCurrency.values())
                .filter(itemCurrency -> itemCurrency.getName().equals(name))
                .findAny();

    }

}
