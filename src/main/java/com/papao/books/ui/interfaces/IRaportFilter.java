package com.papao.books.ui.interfaces;

public interface IRaportFilter {

    void addFilters();

    void resetFilters();

    boolean validateFilters();

    String parse();

}
