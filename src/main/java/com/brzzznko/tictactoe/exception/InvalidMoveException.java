package com.brzzznko.tictactoe.exception;

public class InvalidMoveException extends RuntimeException {

    private static final String MESSAGE = "Invalid move!";

    public InvalidMoveException() {
        super(MESSAGE);
    }
}
