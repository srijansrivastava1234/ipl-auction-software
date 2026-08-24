package com.ipl.auction.exception;

public class PlayerNotAvailableException extends RuntimeException {
    public PlayerNotAvailableException(String message) {
        super(message);
    }
}
