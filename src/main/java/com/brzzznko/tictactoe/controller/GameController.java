package com.brzzznko.tictactoe.controller;

import com.brzzznko.tictactoe.service.BoardService;
import com.brzzznko.tictactoe.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final BoardService boardService;
    private final GameService gameService;

    @GetMapping("/state")
    public String getGameState() {
        return gameService.getStatus().getDescription() + "\n" + boardService.getBoardVisualised();
    }
}
