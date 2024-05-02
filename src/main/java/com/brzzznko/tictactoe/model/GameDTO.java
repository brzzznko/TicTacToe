package com.brzzznko.tictactoe.model;

import com.brzzznko.tictactoe.utility.Sign;
import com.brzzznko.tictactoe.utility.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
public class GameDTO {

    private Character enemySign;
    private Status gameStatus;
    private Character[] board;

}

