package com.orderManagement.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class BooleanConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean value) {
        if (value != null) {
            if (value) {
                return "1";
            } else {
                return "0";
            }

        }
        return null;
    }
    @Override
    public Boolean convertToEntityAttribute(String value) {
        if (value != null) {
            return !value.equals( "0");
        }
        return null;
    }
}
