package com.concert.seatbooking.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }

    public NotFoundException(String message) {
        super(message);
    }
}