package com.availability_manager.model.enumerate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ItemType {

    ABSENCE( "TELEWORK"),
    SICKLEAVE("BANKDAY"),
    HOLIDAY("BANKDAY"),
    TELEWORK("ABSENCE"),
    BANKDAY("ABSENCE", "SICKLEAVE", "HOLIDAY", "TELEWORK");

    private final List<String> compatibles;

    ItemType(String... compatibles) {
        this.compatibles = Arrays.asList(compatibles);
    }

    public List<ItemType> getCompatibles() {
        return compatibles.stream()
                .map(this::convertToItemType)
                .collect(Collectors.toList());
    }

    private ItemType convertToItemType(String itemType) {
        return ItemType.valueOf(itemType.toUpperCase());
    }

}