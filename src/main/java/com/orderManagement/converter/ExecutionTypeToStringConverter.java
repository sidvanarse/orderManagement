package com.orderManagement.converter;

import com.orderManagement.model.ExecutionType;
import com.orderManagement.model.OrderType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class ExecutionTypeToStringConverter implements AttributeConverter<ExecutionType, String> {
    @Override
    public String convertToDatabaseColumn(ExecutionType executionType) {
        if (executionType.equals(ExecutionType.OFFER)) {
            return ExecutionType.OFFER.name();
        } else {
            return ExecutionType.ASK.name();
        }
    }

    @Override
    public ExecutionType convertToEntityAttribute(String value) {
        if(value.equalsIgnoreCase(ExecutionType.OFFER.name())){
            return ExecutionType.OFFER;
        }
        return ExecutionType.ASK;
    }
}
