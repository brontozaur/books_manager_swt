package com.papao.books.view.providers.tree;

import java.util.List;

public class IntValuePairsWrapper {

    private int validDistinctValues;
    private List<IntValuePair> pairs;

    public IntValuePairsWrapper(int validDistinctValues, List<IntValuePair> pairs) {
        this.validDistinctValues = validDistinctValues;
        this.pairs = pairs;
    }

    public int getValidDistinctValues() {
        return validDistinctValues;
    }

    public List<IntValuePair> getPairs() {
        return pairs;
    }
}
