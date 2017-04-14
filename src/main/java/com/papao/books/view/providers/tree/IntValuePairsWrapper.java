package com.papao.books.view.providers.tree;

import java.util.List;

public class IntValuePairsWrapper {

    private int totalCount;
    private List<IntValuePair> pairs;

    public IntValuePairsWrapper(int totalCount, List<IntValuePair> pairs) {
        this.totalCount = totalCount;
        this.pairs = pairs;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<IntValuePair> getPairs() {
        return pairs;
    }
}
