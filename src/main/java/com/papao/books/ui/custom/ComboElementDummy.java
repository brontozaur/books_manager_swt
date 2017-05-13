package com.papao.books.ui.custom;

public class ComboElementDummy implements ComboElement {

    private String text;

    public ComboElementDummy(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
