package com.brzzznko.tictactoe.controller;

import com.brzzznko.tictactoe.utility.Sign;
import com.brzzznko.tictactoe.model.MoveDTO;
import com.brzzznko.tictactoe.service.GameService;
import com.brzzznko.tictactoe.utility.WebSocketApiConstants;
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


    @MessageMapping(WebSocketApiConstants.MOVE)
    public void handleMove(MoveDTO move) {
        try {
            service.acceptMove(move);
        } catch (RuntimeException e) {
            template.convertAndSend(WebSocketApiConstants.DESTINATION_MOVE_REJECTED, move);
        }

        template.convertAndSend(WebSocketApiConstants.DESTINATION_MOVE_ACCEPTED, move);
    }

    @MessageMapping(WebSocketApiConstants.MOVE_REQUEST)
    @SendTo(WebSocketApiConstants.DESTINATION_MOVE_REQUESTED)
    public MoveDTO handleMoveRequest() {
        return service.proposeMove();
    }

    @MessageMapping(WebSocketApiConstants.MOVE_ACCEPTED)
    public void handleAcceptedMove(MoveDTO move) {
        service.acceptMove(move);
    }

    @MessageMapping(WebSocketApiConstants.GAME_JOIN)
    @SendTo(WebSocketApiConstants.DESTINATION_GET_SIGN)
    public Character handleJoiningRequest() {
        // TODO check if game started
        service.setPlayerSign(Sign.X);
        return Sign.O;
    }
}

