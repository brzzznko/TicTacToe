package com.brzzznko.tictactoe.service;

import com.brzzznko.tictactoe.model.GameDTO;
import com.brzzznko.tictactoe.model.MoveDTO;
import com.brzzznko.tictactoe.utility.Sign;
import com.brzzznko.tictactoe.utility.Status;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompSession;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameServiceTest {

    BoardService boardService = new BoardService();
    StompSession session = Mockito.mock(StompSession.class);
    GameService service = new GameService(boardService);

    @Test
    @Order(1)
    public void gameStartedTest() {
        Assertions.assertEquals(Status.WAITING, service.getStatus());
    }

    @Test
    @Order(2)
    public void connectToServerTest() {
        Character[] emptyBoard = {
                ' ', ' ', ' ',
                ' ', ' ', ' ',
                ' ', ' ', ' '
        };

        service.nextStep(session);
        service.acceptGameState(GameDTO.builder()
                .gameStatus(Status.WAITING)
                .enemySign(Sign.X)
                .board(emptyBoard)
                .build());

        Assertions.assertEquals(Status.RECEIVED_MOVE, service.getStatus());
    }

    @Test
    @Order(3)
    public void proposeMoveTest() {
        service.nextStep(session);
        Assertions.assertEquals(Status.WAITING_ACCEPT, service.getStatus());
    }

    @Test
    @Order(4)
    public void receiveAcceptTest() {
        service.acceptMove(getMove(4, Sign.O));
        Assertions.assertEquals(Status.RECEIVED_ACCEPT, service.getStatus());
    }

    @Test
    @Order(5)
    public void requestMoveTest() {
        service.nextStep(session);
        Assertions.assertEquals(Status.REQUESTED_MOVE, service.getStatus());
    }

    @Test
    @Order(6)
    public void receiveMoveTest() {
        service.acceptRequestedMove(session, getMove(0, Sign.X));
        Assertions.assertEquals(Status.RECEIVED_MOVE, service.getStatus());
    }

    @Test
    @Order(7)
    public void restartInstanceTest() {
        boardService = new BoardService();
        service = new GameService(boardService);
        Assertions.assertEquals(Status.WAITING, service.getStatus());
    }

    @Test
    @Order(8)
    public void reconnectTest() {
        Character[] board = {
                'X', 'O', ' ',
                'X', 'O', ' ',
                ' ', ' ', ' '
        };

        service.nextStep(session);
        service.acceptGameState(GameDTO.builder()
                .gameStatus(Status.RECEIVED_ACCEPT)
                .enemySign(Sign.X)
                .board(board)
                .build());

        Assertions.assertEquals(Status.RECEIVED_MOVE, service.getStatus());
    }

    @Test
    @Order(9)
    public void winTest() {
        service.nextStep(session);
        Assertions.assertEquals(Status.WAITING_ACCEPT, service.getStatus());

        service.acceptMove(getMove(7, Sign.O));
        Assertions.assertEquals(Status.WON, service.getStatus());
    }

    @Test
    @Order(10)
    public void drawTest() {
        // disconnect
        boardService = new BoardService();
        service = new GameService(boardService);

        // Accept game from the server
        Character[] board = {
                'X', 'O', 'X',
                'X', 'O', 'X',
                'O', 'X', ' '
        };

        service.nextStep(session);
        service.acceptGameState(GameDTO.builder()
                .gameStatus(Status.RECEIVED_ACCEPT)
                .enemySign(Sign.X)
                .board(board)
                .build());

        // Propose last move
        service.nextStep(session);
        service.acceptMove(getMove(8, Sign.O));
        Assertions.assertEquals(Status.DRAW, service.getStatus());
    }

    private MoveDTO getMove(int index, Character sign) {
        return MoveDTO.builder().index(index).sign(sign).build();
    }

}