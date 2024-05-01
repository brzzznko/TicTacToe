package com.brzzznko.tictactoe.controller;

import com.brzzznko.tictactoe.enumeration.Sign;
import com.brzzznko.tictactoe.model.MoveDTO;
import com.brzzznko.tictactoe.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Profile("server")
public class WebSocketController {

    private final GameService service;


    @MessageMapping("/move")
    @SendTo("/topic/accepted/move")
    public MoveDTO handleMove(MoveDTO move) throws InterruptedException {
        Thread.sleep(5000);
        service.acceptMove(move);
        return move;
    }

    @MessageMapping("/move/request")
    @SendTo("/topic/requested/move")
    public MoveDTO handleMoveRequest() throws InterruptedException {
        Thread.sleep(5000);
        return service.proposeMove();
    }

    @MessageMapping("/move/accepted")
    public void handleAcceptedMove(MoveDTO move) throws InterruptedException {
        Thread.sleep(5000);

        service.acceptMove(move);
    }

    @MessageMapping("/game/join")
    @SendTo("/topic/sign")
    public Character handleJoiningRequest() throws InterruptedException {
        Thread.sleep(5000);

        // TODO check if game started
        service.setPlayerSign(Sign.X);
        return Sign.O;
    }
}

