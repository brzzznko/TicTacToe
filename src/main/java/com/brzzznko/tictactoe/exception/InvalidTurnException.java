package com.brzzznko.tictactoe.exception;

public class InvalidTurnException extends RuntimeException {

    private static final String MESSAGE = "Invalid turn!";

    public InvalidTurnException() {
        super(MESSAGE);
    }
}
