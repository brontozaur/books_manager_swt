package com.papao.books.view.interfaces;

public interface IRaportFilter {

    void addFilters();

    void resetFilters();

    boolean validateFilters();

    String parse();

}
