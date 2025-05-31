package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import lombok.Getter;
import lombok.Setter;


public class GameManager {

    @Getter
    @Setter
    private final GameBoard gameBoard;
    @Getter
    @Setter
    private Player activePlayer;
    @Getter
    private int roundCounter = 0;

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
            gameBoard.placeHouse(activePlayer, position, roundCounter);
        } else {
            throw new IllegalArgumentException("Ungültige Position für das Platzieren des Hauses: " + position);
        }
    }

    public void nextRound() {
        roundCounter++;
    }
}
