package com.ipl.auction.exception;

public class InsufficientPurseException extends RuntimeException {
    public InsufficientPurseException(String message) {
        super(message);
    }
}
