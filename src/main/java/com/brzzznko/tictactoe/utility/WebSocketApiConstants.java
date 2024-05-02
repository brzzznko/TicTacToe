package com.brzzznko.tictactoe.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WebSocketApiConstants {

    public final String DESTINATION_PREFIX = "/topic";
    public final String APP_PREFIX = "/app";
    public final String CONNECTION_PREFIX = "/connection";

    public final String DESTINATION_MOVE_REQUESTED = DESTINATION_PREFIX + "/move/requested";
    public final String DESTINATION_MOVE_ACCEPTED = DESTINATION_PREFIX + "/move/accepted";
    public final String DESTINATION_MOVE_REJECTED = DESTINATION_PREFIX + "/move/error";
    public final String DESTINATION_GAME_JOIN = DESTINATION_PREFIX + "/game/join/response";

    public final String MOVE = "/move";
    public final String MOVE_REQUEST = "/move/request";
    public final String MOVE_ACCEPTED = "/move/accepted";
    public final String GAME_JOIN = "/game/join";

    public final String APP_MOVE = APP_PREFIX + MOVE;
    public final String APP_MOVE_REQUEST = APP_PREFIX + MOVE_REQUEST;
    public final String APP_MOVE_ACCEPTED = APP_PREFIX + MOVE_ACCEPTED;
    public final String APP_GAME_JOIN = APP_PREFIX + GAME_JOIN;

}
