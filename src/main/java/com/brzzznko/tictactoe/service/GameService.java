package com.brzzznko.tictactoe.service;

import com.brzzznko.tictactoe.model.GameDTO;
import com.brzzznko.tictactoe.utility.Sign;
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
    private volatile Status beforeDisconnectStatus = Status.WAITING;

    public void nextStep(StompSession session) {
        switch (status) {
            case WAITING, DISCONNECTED -> joinGame(session);
            case RECEIVED_ACCEPT -> requestMove(session);
            case RECEIVED_MOVE -> proposeMove(session);
            case REJECT_REQUIRED -> sendReject();
        }
    }

    public GameDTO handleJoinRequest(GameDTO gameInfo) {
        log.info("Another instance is joining the game");

        if (status == Status.WAITING) {
            if (gameInfo.getGameStatus() == Status.WAITING) {
                setPlayerSign(Sign.X);

                return gameInfo.toBuilder().
                        enemySign(Sign.X)
                        .build();
            } else {
                acceptGameState(gameInfo);
                return gameInfo;
            }
        } else {
            return GameDTO.builder()
                    .enemySign(service.getCurrentSign())
                    .board(service.getBoard())
                    .gameStatus(beforeDisconnectStatus)
                    .build();
        }
    }

    public void handleDisconnect() {
        log.info("Other instance has disconnected...");
        beforeDisconnectStatus = Status.valueOf(status.name());
        status = Status.DISCONNECTED;
    }

    public void acceptGameState(GameDTO gameInfo) {
        if (status.equals(Status.WAITING)) {
            log.info("Accepted game state from other instance");
            service.fillBoard(gameInfo.getBoard());
            status = getStatusFromEnemy(gameInfo.getGameStatus());
            setPlayerSign(getSignFromEnemy(gameInfo.getEnemySign()));
        } else if (status.equals(Status.DISCONNECTED)) {
            log.info("Joined. Game continues!");
            status = Status.valueOf(beforeDisconnectStatus.name());
        }

        checkGameEnd();
    }

    private void setPlayerSign(Character sign) {
        log.info("The current player has selected the sign: {}", sign);
        service.setCurrentSign(sign);
    }

    private Character getSignFromEnemy(Character enemySign) {
        if (enemySign.equals(Sign.X)) {
            return Sign.O;
        }

        return Sign.X;
    }

    private Status getStatusFromEnemy(Status status) {
        return switch (status) {
            case WAITING, RECEIVED_ACCEPT, DISCONNECTED -> Status.RECEIVED_MOVE;
            case REJECT_REQUIRED, WAITING_ACCEPT, REQUESTED_MOVE -> Status.REJECT_REQUIRED;
            case RECEIVED_MOVE -> Status.RECEIVED_ACCEPT;
            case WON -> Status.LOST;
            case LOST -> Status.WON;
            case DRAW -> Status.DRAW;
        };
    }

    private void joinGame(StompSession session) {
        log.info("Sending join request to the server...");

        GameDTO gameInfo = GameDTO.builder()
                .gameStatus(beforeDisconnectStatus)
                .enemySign(service.getCurrentSign())
                .board(service.getBoard()).build();

        session.send(WebSocketApiConstants.APP_GAME_JOIN, gameInfo);
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

    public void handleMove(MoveDTO move) {
        service.makeMove(move.getIndex(), move.getSign());
        log.info("{} has been handled", move);
        status = Status.RECEIVED_MOVE;
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

    private void sendReject() {
        log.info("Rejecting losted move from the server");
        status = Status.RECEIVED_ACCEPT;
    }

    public void checkGameEnd(Character sign) {
        if (service.isWinner(sign)) {
            defineWinner(sign);
        } else if (service.isDraw()) {
            log.info("The game ended in a draw!");
            status = Status.DRAW;
        }
    }

    public void checkGameEnd() {
        if (service.isWinner(Sign.X)) {
            defineWinner(Sign.X);
        } else if (service.isWinner(Sign.O)) {
            defineWinner(Sign.O);
        } else if (service.isDraw()) {
            log.info("The game ended in a draw!");
            status = Status.DRAW;
        }
    }

    private void defineWinner(Character sign) {
        if (sign.equals(service.getCurrentSign())) {
            log.info("Current player {} won the game!", sign);
            status = Status.WON;
        } else {
            log.info("Other player {} won the game", sign);
            status = Status.LOST;
        }
    }
}
