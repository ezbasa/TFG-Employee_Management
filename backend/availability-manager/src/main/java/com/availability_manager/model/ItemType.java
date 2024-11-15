package com.dekra.availability_manager.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ItemType {

    AUSENCIA( "TELETRABAJO"),
    BAJA("FESTIVO"),
    VACACIONES("FESTIVO"),
    TELETRABAJO("AUSENCIA"),
    FESTIVO("AUSENCIA", "BAJA", "VACACIONES", "TELETRABAJO");

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