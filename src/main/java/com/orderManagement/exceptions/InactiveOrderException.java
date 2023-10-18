package com.orderManagement.exceptions;

public class InactiveOrderException extends RuntimeException {
    public InactiveOrderException(String message) {
        super(message);
    }
}
