package com.brzzznko.tictactoe.service;

import com.brzzznko.tictactoe.enumeration.Status;
import com.brzzznko.tictactoe.model.MoveDTO;
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

    public synchronized void nextStep(StompSession session) {
        log.info(status.getDescription());
        switch (status) {
            case WAITING -> getPlayerSign(session);
            case RECEIVED_ACCEPT -> requestMove(session);
            case RECEIVED_MOVE -> proposeMove(session);
        }
    }

    private synchronized void getPlayerSign(StompSession session) {
        log.info("Asking server to get player sign");
        session.send("/app/game/join", "");
    }

    public synchronized void setPlayerSign(Character sign) {
        log.info("Current player will use sign: {}", sign);
        service.setCurrentSign(sign);
        status = Status.RECEIVED_MOVE;
    }

    private synchronized void proposeMove(StompSession session) {
        MoveDTO move = MoveDTO.builder()
                .sign(service.getCurrentSign())
                .index(new Random().nextInt(0, 9))
                .build();
        
        log.info("Proposing move to the server: {}", move);

        status = Status.WAITING_ACCEPT;
        session.send("/app/move", move);
    }

    public synchronized MoveDTO proposeMove() {
        MoveDTO move = MoveDTO.builder()
                .sign(service.getCurrentSign())
                .index(new Random().nextInt(0, 9))
                .build();

        log.info("Proposing move to the client: {}", move);
        status = Status.WAITING_ACCEPT;

        return move;
    }

    public synchronized void acceptMove(MoveDTO move) {
        service.makeMove(move.getIndex(), move.getSign());
        log.info("Accepted move: {}", move);
        status = Status.RECEIVED_ACCEPT;
    }
    
    private synchronized void requestMove(StompSession session) {
        log.info("Asking server to make a move");
        session.send("/app/move/request", "");
        status = Status.REQUESTED_MOVE;
    }

    public synchronized void acceptRequestedMove(StompSession session, MoveDTO move) {
        log.info("Move from server: {}", move);
        service.makeMove(move.getIndex(), move.getSign());
        status = Status.RECEIVED_MOVE;
        session.send("/app/move/accepted", move);
    }
    
}
