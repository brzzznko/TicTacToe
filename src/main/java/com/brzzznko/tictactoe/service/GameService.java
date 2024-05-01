package com.brzzznko.tictactoe.service;

import com.brzzznko.tictactoe.utility.Status;
import com.brzzznko.tictactoe.exception.InvalidMoveException;
import com.brzzznko.tictactoe.model.MoveDTO;
import com.brzzznko.tictactoe.utility.WebSocketApiConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class GameService {

    private final BoardService service;

    private volatile Status status = Status.WAITING;

    public void nextStep(StompSession session) {
        switch (status) {
            case WAITING -> getPlayerSign(session);
            case RECEIVED_ACCEPT -> requestMove(session);
            case RECEIVED_MOVE -> proposeMove(session);
        }
    }

    public void setPlayerSign(Character sign) {
        log.info("The current player has selected the sign: {}", sign);
        service.setCurrentSign(sign);
        status = Status.RECEIVED_MOVE;
    }

    private void getPlayerSign(StompSession session) {
        log.info("Requesting player's sign from the server");
        session.send(WebSocketApiConstants.APP_GAME_JOIN, "");
    }

    private void proposeMove(StompSession session) {
        MoveDTO move = MoveDTO.builder()
                .sign(service.getCurrentSign())
                .index(new Random().nextInt(0, 9))
                .build();

        log.info("Proposing move to the server: {}", move);

        status = Status.WAITING_ACCEPT;
        session.send(WebSocketApiConstants.APP_MOVE, move);
    }

    public MoveDTO proposeMove() {
        MoveDTO move = MoveDTO.builder()
                .sign(service.getCurrentSign())
                .index(new Random().nextInt(0, 9))
                .build();

        log.info("Proposing move to the client: {}", move);
        status = Status.WAITING_ACCEPT;

        return move;
    }

    public void acceptMove(MoveDTO move) {
        service.makeMove(move.getIndex(), move.getSign());
        log.info("{} has been accepted", move);
        status = Status.RECEIVED_ACCEPT;
        checkGameEnd(move.getSign());
    }

    private void requestMove(StompSession session) {
        log.info("Asking server to make a move");
        session.send(WebSocketApiConstants.APP_MOVE_REQUEST, "");
        status = Status.REQUESTED_MOVE;
    }

    public void acceptRequestedMove(StompSession session, MoveDTO move) {
        log.info("Received move from the server: {}", move);

        try {
            service.makeMove(move.getIndex(), move.getSign());
            log.info("{} has been accepted", move);
            status = Status.RECEIVED_MOVE;
            checkGameEnd(move.getSign());
            session.send(WebSocketApiConstants.APP_MOVE_ACCEPTED, move);

        } catch (InvalidMoveException e) {
            log.error(e.getMessage());
            log.info("Rejecting invalid {} from the server", move);
            status = Status.RECEIVED_ACCEPT;
        }
    }

    public void rejectMove(MoveDTO move) {
        log.info("{} was rejected by server", move);
        status = Status.RECEIVED_MOVE;
    }

    public void checkGameEnd(Character sign) {
        if (service.checkWinner(sign)) {
            if (sign.equals(service.getCurrentSign())) {
                log.info("Current player {} won the game!", sign);
                status = Status.WON;
            } else {
                log.info("Other player {} won the game", sign);
                status = Status.LOST;
            }

        } else if (service.checkDraw()) {
            log.info("The game ended in a draw!");
            status = Status.DRAW;
        }
    }
}
