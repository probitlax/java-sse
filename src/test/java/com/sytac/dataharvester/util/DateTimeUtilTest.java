package com.sytac.dataharvester.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilTest {

    @ParameterizedTest
    @CsvSource(value = {
            "01-01-2023 11:00:00.000,US,01-01-2023 20:00:00.000",
            "01-01-2023 11:00:00.000,JP,01-01-2023 03:00:00.000"
    }, delimiter = ',')
    void testConvertTime_toCET(String inputDateTime, String countryShortName, String expectedTime) {

        String convertedDateTime = DateTimeUtil.convertTime(inputDateTime, countryShortName, "CET");

        assertEquals(expectedTime, convertedDateTime);
    }

    @Test
    void testConvertTime_InvalidInput() {
        String inputDateTime = "01-01-2023 11:00:00.000";
        String invalidCountryCode = "INVALID";

        assertThrows(IllegalArgumentException.class,
                () -> DateTimeUtil.convertTime(inputDateTime, invalidCountryCode, "CET"),
                "Expected IllegalArgumentException for invalid country code");
    }

}
