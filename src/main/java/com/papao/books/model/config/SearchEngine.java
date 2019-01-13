package com.papao.books.model.config;

import org.eclipse.swt.widgets.Display;

public enum SearchEngine {
    LIBRARIE_NET(0, "librarie.net", "http://www.librarie.net/cautare-rezultate.php?t=QQQ"),
    ELEFANT_RO(1, "elefant.ro", "http://www.elefant.ro/search?query=QQQ"),
    LIBRIS_RO(2, "libris.ro", "http://www.libris.ro/?sn.q=QQQ"),
    LIBRARIASOPHIA_RO(3, "sophia.ro", "http://www.librariasophia.ro/cautare.html?mod=cautarerapida&cautare_toate=&cautare_multimedia=&cautare_promotii=&cautare_reducere50=&cautare_titluri=&cautare_autori=&cautare_edituri=&filtru=QQQ"),
    EGUMENITA_RO(4, "egumenita.ro", "https://egumenita.ro/cauta.php?search-box=QQQ"),
    GOOGLE_COM(5, "google.com", "https://www.google.ro/search?tbm=isch&biw=" +
            Display.getCurrent().getPrimaryMonitor().getBounds().width + "&bih=" +
            Display.getCurrent().getPrimaryMonitor().getBounds().height + "&q=QQQ&oq=QQQ");

    private int ordinal;
    private String name;
    private String query;

    public static final String QUERY_PLACEHOLDER = "QQQ";

    SearchEngine(int ordinal, String name, String query) {
        this.ordinal = ordinal;
        this.name = name;
        this.query = query;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public static String[] getComboItems() {
        return new String[]{SearchEngine.LIBRARIE_NET.getName(),
                SearchEngine.ELEFANT_RO.getName(),
                SearchEngine.LIBRIS_RO.getName(),
                SearchEngine.LIBRARIASOPHIA_RO.getName(),
                SearchEngine.EGUMENITA_RO.getName(),
                SearchEngine.GOOGLE_COM.getName()};
    }

    public static String getQueryByComboIndex(int comboIndex) {
        if (comboIndex == SearchEngine.LIBRARIE_NET.getOrdinal()) {
            return SearchEngine.LIBRARIE_NET.getQuery();
        } else if (comboIndex == SearchEngine.ELEFANT_RO.getOrdinal()) {
            return SearchEngine.ELEFANT_RO.getQuery();
        } else if (comboIndex == SearchEngine.LIBRIS_RO.getOrdinal()) {
            return SearchEngine.LIBRIS_RO.getQuery();
        } else if (comboIndex == SearchEngine.LIBRARIASOPHIA_RO.getOrdinal()) {
            return SearchEngine.LIBRARIASOPHIA_RO.getQuery();
        } else if (comboIndex == SearchEngine.EGUMENITA_RO.getOrdinal()) {
            return SearchEngine.EGUMENITA_RO.getQuery();
        } else {
            return SearchEngine.GOOGLE_COM.getQuery();
        }
    }
}
