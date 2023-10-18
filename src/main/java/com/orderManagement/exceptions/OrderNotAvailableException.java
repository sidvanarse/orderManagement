package com.orderManagement.exceptions;

public class OrderNotAvailableException extends RuntimeException {
    public OrderNotAvailableException(String message) {
        super(message);
    }
}
