package at.aau.serg.kingdombuilderserver.game;

import java.util.List;

public class WinningConditionEvaluator {
   //Platzhalterklasse soll durch echtes Gameboard ersetzt werden
    private GameBoardWinningConditions board;
    private List<Player> players;

    public WinningConditionEvaluator(GameBoardWinningConditions board, List<Player> players) {
        this.board = board;
        this.players = players;
    }
}
