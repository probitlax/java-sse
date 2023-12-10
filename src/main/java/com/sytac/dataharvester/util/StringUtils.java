package com.sytac.dataharvester.util;

import java.util.Arrays;

public class StringUtils {
    private StringUtils() {
    }

    public static String getFirstElement(String commaSeparatedString) {
        return (commaSeparatedString != null && !commaSeparatedString.isEmpty()) ?
                Arrays.stream(commaSeparatedString.split(","))
                        .findFirst()
                        .orElse("No elements found") : "";
    }
}
