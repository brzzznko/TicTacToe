package com.brzzznko.tictactoe.service;

import com.brzzznko.tictactoe.utility.Sign;
import com.brzzznko.tictactoe.exception.InvalidMoveException;
import com.brzzznko.tictactoe.exception.InvalidTurnException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class BoardService {

    private final Character[] board = {
            ' ', ' ', ' ',
            ' ', ' ', ' ',
            ' ', ' ', ' '
    };

    private final int[][] winCombinations = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}
    };

    private char currentSign = Sign.EMPTY;

    private char lastPlayed = Sign.EMPTY;


    public void makeMove(int index, char sign) {
        checkTurn(sign);
        validateMove(index);

        board[index] = sign;
        lastPlayed = sign;
    }

    public String getBoardVisualised() {
        int count = 0;
        StringBuilder display = new StringBuilder("\n ");

        for (char c : board) {
            if (count == 3) {
                display.append("\n--- --- --- \n ");
                count = 0;
            }

            display.append(c).append(" | ");
            count++;
        }

        return display.toString();
    }

    public void fillBoard(Character[] boardSample) {
        if (lastPlayed != Sign.EMPTY) {
            throw new IllegalStateException("Can't fill the board. It's not empty!");
        }

        System.arraycopy(boardSample, 0, board, 0, board.length);
    }

    public void setCurrentSign(char currentSign) {
        if (Sign.EMPTY == this.currentSign) {
            this.currentSign = currentSign;

        }
    }

    private void validateMove(int index) {
        if (index < 0 || index > 9 || board[index] != ' ') {
            throw new InvalidMoveException();
        }
    }

    private void checkTurn(Character sign) {
        if (lastPlayed == sign) {
            throw new InvalidTurnException();
        }
    }

    public boolean isWinner(Character sign) {
        for (int[] winCombination : winCombinations) {
            int count = 0;

            for (int i : winCombination) {
                if (board[i] == sign) {
                    count++;
                }
            }

            if (count == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean isDraw() {
        for (Character sign : board) {
            if (sign == Sign.EMPTY) {
                return false;
            }
        }

        return true;
    }
}
