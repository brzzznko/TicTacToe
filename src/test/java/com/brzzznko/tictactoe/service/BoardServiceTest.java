package com.brzzznko.tictactoe.service;

import com.brzzznko.tictactoe.exception.InvalidMoveException;
import com.brzzznko.tictactoe.exception.InvalidTurnException;
import com.brzzznko.tictactoe.utility.Sign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class BoardServiceTest {

    BoardService service = new BoardService();

    @Test
    public void makeMoveTest() {
        service.makeMove(0, Sign.O);
        service.makeMove(1, Sign.X);
        service.makeMove(3, Sign.O);
        service.makeMove(6, Sign.X);

        char [] boardExpected = {
                'O', 'X', ' ',
                'O', ' ', ' ',
                'X', ' ', ' '
        };

        Assertions.assertArrayEquals(boardExpected, service.getBoard());
    }

    @Test
    public void checkWinnerOnWinCombination() {
        service.makeMove(0, Sign.X);
        service.makeMove(1, Sign.O);
        service.makeMove(4, Sign.X);
        service.makeMove(2, Sign.O);
        service.makeMove(8, Sign.X);

        Assertions.assertTrue(service.checkWinner(Sign.X));
    }

    @Test
    public void checkWinnerOnNotWinCombination() {
        service.makeMove(0, Sign.X);

        Assertions.assertFalse(service.checkWinner(Sign.O));
    }

    @Test
    public void checkWinnerOnEmptyField() {
        Assertions.assertFalse(service.checkWinner(Sign.O));
    }

    @Test
    public void checkDrawOnFullBoard() {
        service.makeMove(0, Sign.X);
        service.makeMove(1, Sign.O);
        service.makeMove(4, Sign.X);
        service.makeMove(2, Sign.O);
        service.makeMove(8, Sign.X);
        service.makeMove(3, Sign.O);
        service.makeMove(5, Sign.X);
        service.makeMove(6, Sign.O);
        service.makeMove(7, Sign.X);

        Assertions.assertTrue(service.checkDraw());
    }

    @Test
    public void checkDrawInMidGame() {
        service.makeMove(0, Sign.X);
        service.makeMove(1, Sign.O);
        service.makeMove(4, Sign.X);
        service.makeMove(2, Sign.O);

        Assertions.assertFalse(service.checkDraw());
    }

    @Test
    public void checkDrawOnEmptyField() {
        Assertions.assertFalse(service.checkDraw());
    }

    @Test
    public void makingSecondMoveShouldThrowException () {
        service.makeMove(0, Sign.O);

        Assertions.assertThrows(InvalidTurnException.class, () -> service.makeMove(1, Sign.O));
    }

    @Test
    public void makingSameMoveAgainShouldThrowException () {
        service.makeMove(0, Sign.O);
        service.makeMove(1, Sign.X);

        Assertions.assertThrows(InvalidMoveException.class, () -> service.makeMove(0, Sign.O));
    }

    @Test
    public void makingMoveWithInvalidIndexShouldThrowException () {
        Assertions.assertThrows(InvalidMoveException.class, () -> service.makeMove(11, Sign.O));
    }

}