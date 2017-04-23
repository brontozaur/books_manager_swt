package com.papao.books.view.searcheable;

public class RatingSearch extends AbstractSearchType {

    public RatingSearch(BorgSearchSystem searchSystem, String colName) {
        super(searchSystem, colName);
    }

    @Override
    protected void createContents() {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean compareValues(Object valueToBeCompared) {
        if (valueToBeCompared == null) {
            return true;
        }
        return compareNumbers(0, 5, Double.valueOf(valueToBeCompared.toString()));
    }
}
