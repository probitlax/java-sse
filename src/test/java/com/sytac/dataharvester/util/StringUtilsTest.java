package com.sytac.dataharvester.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
    test1,test2,test3 | test1   
    test1             | test1 
    ''                | ''            
    """)
    void testGetFirstElement_WithNonNullInput(String input, String output) {
        String firstElement = StringUtils.getFirstElement(input);
        assertEquals(output, firstElement);
    }
}
