package com.brzzznko.tictactoe.utility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    WAITING("Waiting players to start game..."),
    REQUESTED_MOVE("Requested move from other instance."),
    RECEIVED_MOVE("Received move from other instance."),
    WAITING_ACCEPT("Sent move, waiting for accept message."),
    RECEIVED_ACCEPT("Received accept message."),
    WON("Current instance won the game!"),
    LOST("Current instance lost the game!"),
    DRAW("The game ended in a draw!");

    private final String description;
}
