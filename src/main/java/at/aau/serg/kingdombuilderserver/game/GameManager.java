package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GameManager {

    @Getter
    @Setter
    private final GameBoard gameBoard;
    @Getter
    @Setter
    private Player activePlayer;
    @Getter
    private int roundCounter = 0;
    @Getter
    private boolean awaitingCheatReports = false;
    private Set<String> reports = new HashSet<>();
    private Map<String, List<String>> cheatReportsThisTurn;

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

            activePlayer.getHousesPlacedThisTurn().add(position);
        } else {
            throw new IllegalArgumentException("Ungültige Position für das Platzieren des Hauses: " + position);
        }
    }

    public void nextRound() {
        roundCounter++;
        if (activePlayer != null) {
            activePlayer.getHousesPlacedThisTurn().clear();
        }
    }

    public void registerCheatReport (String reporterId){
        if (awaitingCheatReports){
            reports.add(reporterId);
        }
    }

    public void processCheatReportOutcome(){
        Player active = getActivePlayer();

        if (active.hasCheated() && !reports.isEmpty()){
            // Cheat wurde korrekt erkannt
            for (GameHousePosition pos: active.getHousesPlacedThisTurn()){
            }
        }
    }
    public void setAwaitingCheatReports(boolean awaitingCheatReports){
        this.awaitingCheatReports = awaitingCheatReports;
        if (awaitingCheatReports){
            this.cheatReportsThisTurn.clear();  // Alte Meldungen für neue Runde löschen
        }
    }



}
