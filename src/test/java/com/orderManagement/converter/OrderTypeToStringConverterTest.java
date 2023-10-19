package com.orderManagement.converter;

import com.orderManagement.model.OrderType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTypeToStringConverterTest {
    OrderTypeToStringConverter orderTypeToStringConverter = new OrderTypeToStringConverter();
    @Test
    public void testConvertToDatabaseColumnIfValueIsTrue(){
        String result = orderTypeToStringConverter.convertToDatabaseColumn(OrderType.BUY);
        assertEquals(result,"BUY");
    }

    @Test
    public void testConvertToDatabaseColumnIfValueIsFalse(){
        String result = orderTypeToStringConverter.convertToDatabaseColumn(OrderType.SELL);
        assertEquals(result,"SELL");
    }

    @Test
    public void testConvertToEntityAttributeIfValueIsTrue(){
        OrderType result = orderTypeToStringConverter.convertToEntityAttribute("BUY");
        assertEquals(result,OrderType.BUY);
    }

    @Test
    public void testConvertToEntityAttributeIfValueIsFalse(){
        OrderType result = orderTypeToStringConverter.convertToEntityAttribute("SELL");
        assertEquals(result,OrderType.SELL);
    }
}
