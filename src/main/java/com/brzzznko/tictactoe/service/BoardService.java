package com.brzzznko.tictactoe.service;

import com.brzzznko.tictactoe.enumeration.Sign;
import com.brzzznko.tictactoe.exception.InvalidMoveException;
import com.brzzznko.tictactoe.exception.InvalidTurnException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class BoardService {

    private volatile char[] board = {
            ' ', ' ', ' ',
            ' ', ' ', ' ',
            ' ', ' ', ' '
    };

    private final int[][] winCombinations = {
            { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 }
    };

    @Setter
    private char currentSign;

    private char lastPlayed;


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

    private void validateMove(int index) {
        if (index < 0 || index > 9 || board[index] != ' ') {
            throw new InvalidMoveException();
        }
    }

    private void checkTurn(char sign) {
        if (lastPlayed == sign) {
            throw new InvalidTurnException();
        }
    }

    public boolean checkWinner(char sign) {
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

    public boolean checkDraw() {
        for (char sign : board) {
            if (sign == Sign.EMPTY) {
                return false;
            }
        }

        return true;
    }
}
