package com.papao.books.ui.util;

import java.util.ArrayList;
import java.util.Collections;
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

    public static List<String> getValuesArray(List<? extends Enum> enumValues) {
        if (enumValues == null) {
            return Collections.emptyList();
        }
        List<String> stringValues = new ArrayList<>();
        for (Enum enumVal : enumValues) {
            stringValues.add(enumVal.name());
        }
        return stringValues;
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

    public static <E extends Enum<E>> Enum<E> getEnum(Class<E> enumClass, String value) {
        value = value.toUpperCase();
        for (Enum<E> enumVal : enumClass.getEnumConstants()) {
            if (value.equals(enumVal.name().toUpperCase())) {
                return enumVal;
            }
        }
        return null;
    }
}
