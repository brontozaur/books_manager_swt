package com.papao.books.export;

public class FieldColumnValue {

    private String fieldName;
    private int align;
    private int width;
    private int order;

    public FieldColumnValue(String fieldName, int align, int width, int order) {
        this.fieldName = fieldName;
        this.align = align;
        this.width = width;
        this.order = order;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getAlign() {
        return align;
    }

    public int getWidth() {
        return width;
    }

    public int getOrder() {
        return order;
    }
}
