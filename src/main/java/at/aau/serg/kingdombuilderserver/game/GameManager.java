package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class GameManager {

    @Getter
    private final GameBoard gameBoard;
    @Getter
    @Setter
    private Player activePlayer;
    @Getter
    private int roundCounter = 0;
    @Getter
    @Setter
    private List<Integer> activeBuildingsSequence = new ArrayList<>();

    public GameManager() {
        // Private constructor to prevent instantiation
        this.gameBoard = new GameBoard();
        gameBoard.buildGameBoard();
    }

    // Zusätzlicher Konstruktor NUR FÜR TESTS (package-private)
    GameManager(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void placeHouse(GameHousePosition position) {
        if (gameBoard.isPositionValid(position)) {
            gameBoard.placeHouse(activePlayer,activeBuildingsSequence, position, roundCounter);
        } else {
            throw new IllegalArgumentException("Ungültige Position für das Platzieren des Hauses: " + position);
        }
    }

    public void nextRound() {
        roundCounter++;
    }
}
