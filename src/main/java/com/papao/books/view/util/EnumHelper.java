package com.papao.books.view.util;

import java.util.ArrayList;
import java.util.List;

public class EnumHelper {

    public static <E extends Enum<E>> String getValuesAsString(Class<E> enumClass) {
        StringBuilder values = new StringBuilder();
        for (Enum<E> enumVal : enumClass.getEnumConstants()) {
            if (values.length() > 0) {
                values.append(", ");
            }
            values.append(enumVal.name());
        }
        return values.toString();
    }

    public static <E extends Enum<E>> List<String> getValuesArray(Class<E> enumClass) {
        List<String> enumValues = new ArrayList<>();
        for (Enum<E> enumVal : enumClass.getEnumConstants()) {
            enumValues.add(enumVal.name());
        }
        return enumValues;
    }

    public static <E extends Enum<E>> boolean matchesEnum(Class<E> enumClass, String valueToBeMatched) {
        if (valueToBeMatched == null) {
            return false;
        }
        valueToBeMatched = valueToBeMatched.toUpperCase();
        for (Enum<E> enumVal : enumClass.getEnumConstants()) {
            if (valueToBeMatched.equals(enumVal.name().toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
