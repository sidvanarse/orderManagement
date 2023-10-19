package com.orderManagement.converter;

import com.orderManagement.model.ExecutionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionTypeToStringConverterTest {
    ExecutionTypeToStringConverter executionTypeToStringConverter = new ExecutionTypeToStringConverter();
    @Test
    public void testConvertToDatabaseColumnIfValueIsTrue(){
        String result = executionTypeToStringConverter.convertToDatabaseColumn(ExecutionType.OFFER);
        assertEquals(result,"OFFER");
    }

    @Test
    public void testConvertToDatabaseColumnIfValueIsFalse(){
        String result = executionTypeToStringConverter.convertToDatabaseColumn(ExecutionType.ASK);
        assertEquals(result,"ASK");
    }

    @Test
    public void testConvertToEntityAttributeIfValueIsTrue(){
        ExecutionType result = executionTypeToStringConverter.convertToEntityAttribute("OFFER");
        assertEquals(result,ExecutionType.OFFER);
    }

    @Test
    public void testConvertToEntityAttributeIfValueIsFalse(){
        ExecutionType result = executionTypeToStringConverter.convertToEntityAttribute("ASK");
        assertEquals(result,ExecutionType.ASK);
    }

}
