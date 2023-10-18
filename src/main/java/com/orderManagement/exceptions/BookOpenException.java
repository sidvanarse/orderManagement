package com.orderManagement.exceptions;

public class BookOpenException extends RuntimeException {
    public BookOpenException(String message) {
        super(message);
    }
}
