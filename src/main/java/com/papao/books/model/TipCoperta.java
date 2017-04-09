package com.papao.books.model;

import java.util.ArrayList;
import java.util.List;

public enum TipCoperta {

    Nespecificat, Brosata, Cartonata, Legata_in_piele;

    public static String[] getComboItems() {
        List<String> items = new ArrayList<>();
        for (TipCoperta value: values()) {
            items.add(value.name());
        }
        return items.toArray(new String[items.size()]);
    }
}
