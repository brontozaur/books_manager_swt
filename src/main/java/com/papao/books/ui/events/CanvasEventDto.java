package com.papao.books.ui.events;

import com.papao.books.ui.searcheable.BookSearchType;

public class CanvasEventDto {

    private BookSearchType searchType;
    private String value;

    public CanvasEventDto(BookSearchType searchType, String value) {
        this.searchType = searchType;
        this.value = value;
    }

    public BookSearchType getSearchType() {
        return searchType;
    }

    public String getValue() {
        return value;
    }
}
