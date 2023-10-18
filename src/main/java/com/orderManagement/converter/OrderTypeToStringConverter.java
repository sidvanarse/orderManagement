package com.orderManagement.converter;

import com.orderManagement.model.OrderType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class OrderTypeToStringConverter implements AttributeConverter<OrderType, String> {
    @Override
    public String convertToDatabaseColumn(OrderType orderType) {
        if (orderType.equals(OrderType.BUY)) {
            return OrderType.BUY.name();
        } else {
            return OrderType.SELL.name();
        }
    }

    @Override
    public OrderType convertToEntityAttribute(String value) {
        if(value.equalsIgnoreCase(OrderType.BUY.name())){
            return OrderType.BUY;
        }
        return OrderType.SELL;
    }
}
