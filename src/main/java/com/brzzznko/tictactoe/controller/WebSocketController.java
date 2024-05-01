package com.brzzznko.tictactoe.controller;

import com.brzzznko.tictactoe.enumeration.Sign;
import com.brzzznko.tictactoe.model.MoveDTO;
import com.brzzznko.tictactoe.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Profile("server")
public class WebSocketController {

    private final GameService service;
    private final SimpMessagingTemplate template;


    @MessageMapping("/move")
    public void handleMove(MoveDTO move) {
        try {
            service.acceptMove(move);
        } catch (RuntimeException e) {
            template.convertAndSend("/topic/error/move", move);
        }

        template.convertAndSend("/topic/accepted/move", move);
    }

    @MessageMapping("/move/request")
    @SendTo("/topic/requested/move")
    public MoveDTO handleMoveRequest() {
        return service.proposeMove();
    }

    @MessageMapping("/move/accepted")
    public void handleAcceptedMove(MoveDTO move) {
        service.acceptMove(move);
    }

    @MessageMapping("/game/join")
    @SendTo("/topic/sign")
    public Character handleJoiningRequest() {
        // TODO check if game started
        service.setPlayerSign(Sign.X);
        return Sign.O;
    }
}

