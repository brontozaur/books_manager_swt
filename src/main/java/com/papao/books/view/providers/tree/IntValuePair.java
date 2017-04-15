package com.papao.books.view.providers.tree;

import com.papao.books.model.Carte;
import org.apache.commons.lang3.StringUtils;

public class IntValuePair {

    private String value;
    private String queryValue;
    private int count;

    public IntValuePair(String value, String queryValue, int count) {
        this.count = count;
        this.queryValue = queryValue;
        this.value = value;
        if (StringUtils.isEmpty(value)) {
            this.value = Carte.REPLACEMENT_FOR_NOT_SET;
        }
    }

    public String getValue() {
        return value;
    }

    public String getQueryValue() {
        return queryValue;
    }

    public int getCount() {
        return count;
    }
}
