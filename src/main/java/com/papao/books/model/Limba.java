package com.papao.books.model;

import java.util.ArrayList;
import java.util.List;

public enum Limba {

    Nespecificat, Romana, Engleza, Franceza, Rusa, Italiana, Germana, Spaniola, Portugheza;

    public static String[] getComboItems() {
        List<String> items = new ArrayList<>();
        for (Limba value: values()) {
            items.add(value.name());
        }
        return items.toArray(new String[items.size()]);
    }
}
