package com.brzzznko.tictactoe.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    WAITING("Waiting players to start game"),
    REQUESTED_MOVE("Requested move from other instance"),
    RECEIVED_MOVE("Received move from instance"),
    WAITING_ACCEPT("Sent move, waiting for accept message"),
    RECEIVED_ACCEPT("Received accept message"),
    CLIENT_WON("Client won the game"),
    SERVER_WON("Server won the game"),
    DRAW("Draw");

    private final String description;
}
