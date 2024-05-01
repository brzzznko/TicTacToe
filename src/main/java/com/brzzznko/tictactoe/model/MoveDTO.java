package com.brzzznko.tictactoe.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@ToString
@Jacksonized
public class MoveDTO {

    private final Character sign;
    private final Integer index;

}
