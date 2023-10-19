package com.orderManagement.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanConverterTest {

    BooleanConverter booleanConverter = new BooleanConverter();

    @Test
    public void testConvertToDatabaseColumnIfValueIsNull(){
        String result = booleanConverter.convertToDatabaseColumn(null);
        assertNull(result);
    }

    @Test
    public void testConvertToDatabaseColumnIfValueIsTrue(){
        String result = booleanConverter.convertToDatabaseColumn(true);
        assertEquals(result,"1");
    }

    @Test
    public void testConvertToDatabaseColumnIfValueIsFalse(){
        String result = booleanConverter.convertToDatabaseColumn(false);
        assertEquals(result,"0");
    }

    @Test
    public void testConvertToEntityAttributeIfValueIsNull(){
        Boolean result = booleanConverter.convertToEntityAttribute(null);
        assertNull(result);
    }

    @Test
    public void testConvertToEntityAttributeIfValueIsTrue(){
        Boolean result = booleanConverter.convertToEntityAttribute("1");
        assertTrue(result);
    }

    @Test
    public void testConvertToEntityAttributeIfValueIsFalse(){
        Boolean result = booleanConverter.convertToEntityAttribute("0");
        assertFalse(result);
    }
}
