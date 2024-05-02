package com.brzzznko.tictactoe.utility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    WAITING("Waiting players to start game..."),
    REQUESTED_MOVE("Requested move from other instance."),
    RECEIVED_MOVE("Received move from other instance."),

    REJECT_REQUIRED("Lost proposed move due to network issues, going to reject move"),
    WAITING_ACCEPT("Sent move, waiting for accept message."),
    RECEIVED_ACCEPT("Received accept message."),
    DISCONNECTED("Lost connection with other instance."),
    WON("Current instance won the game!"),
    LOST("Current instance lost the game!"),
    DRAW("The game ended in a draw!");

    private final String description;
}
