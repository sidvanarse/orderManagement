package com.orderManagement.exceptions;

public class BookClosedException extends RuntimeException {
    public BookClosedException(String message) {
        super(message);
    }
}
