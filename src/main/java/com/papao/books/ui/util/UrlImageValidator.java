package com.papao.books.ui.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlImageValidator {

    private static Pattern pattern;

    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg|tiff|webp))$)";

    static {
        pattern = Pattern.compile(IMAGE_PATTERN);
    }

    /**
     * Validate image with regular expression
     *
     * @param image image for validation
     * @return true valid image, false invalid image
     */
    public static boolean validate(final String image) {

        Matcher matcher = pattern.matcher(image);
        return matcher.matches();

    }
}