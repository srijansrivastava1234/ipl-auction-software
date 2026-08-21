package com.ipl.auction.exception;

public class SquadLimitExceededException extends RuntimeException {
    public SquadLimitExceededException(String message) {
        super(message);
    }
}
