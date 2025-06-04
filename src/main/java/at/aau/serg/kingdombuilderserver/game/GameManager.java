package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import lombok.Getter;
import lombok.Setter;

import java.util.*;


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
        if (activePlayer == null) {
            System.err.println("Kein aktiver Spieler ausgewählt, um ein Haus zu platzieren.");
            return;
        }
        if (!gameBoard.isPositionValid(position)) {
            System.err.println("Ungültige Position (außerhalb des Spielfelds): " + position + " für Spieler " + activePlayer.getId());
            return;
        }
        if (gameBoard.isPositionValid(position)) {
            gameBoard.placeHouse(activePlayer, position, roundCounter);

            activePlayer.getHousesPlacedThisTurn().add(position);
        } else {
            throw new IllegalArgumentException("Ungültige Position für das Platzieren des Hauses: " + position);
        }
        if(activePlayer != null && isPositionValidForPlayer(activePlayer, position)) {
            activePlayer.getHousesPlacedThisTurn().add(position);
            activePlayer.decreaseSettlementsBy(1); // Siedlungen des Spieler reduzieren
            System.out.println("Player" + activePlayer.getId() + " placed house at " + position + ". Remaining: " + activePlayer.getRemainingSettlements());
        } else {
            System.err.println("Invalid house placement attempt by " + (activePlayer != null ? activePlayer.getId() : "null") + " at " + position);
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

    public void recordCheatReport(String reporterPlayerId, String reportedPlayerId){
        if(!awaitingCheatReports){
            // Logik, falss außerhalb der Zeitfenster gemeldet wird (sollte nicht passieren, wenn Controller prüft)
            System.err.println("Attempt to record cheat report outside of allowed window");
            return;
        }
        if (activePlayer != null && activePlayer.getId().equals(reportedPlayerId)){
            this.cheatReportsThisTurn.computeIfAbsent(reportedPlayerId, k -> new ArrayList<>()).add(reportedPlayerId);
        }else{
            System.err.println("Attempt to report non-active player or active player is null.");
        }
    }

    public void cleanupTurn() {
        if (activePlayer != null){
            activePlayer.setHasCheated(false);
            activePlayer.getHousesPlacedThisTurn().clear(); // Liste der in dieser Runde platzierten Häuser für den nächsten Zug zurücksetzen
        }
        this.cheatReportsThisTurn.clear(); // Auch hier die Reports löschen
        System.out.println("Turn cleanup for player" + (activePlayer != null ? activePlayer.getId() : "null"));
    }

    // Hilfmethode, um Spieler anhand der ID zu finden
    private Player getPlayerById(String playerId){
        if (this.activePlayer != null && this.activePlayer.getId().equals(playerId)) return this.activePlayer;
        System.err.println("WARNUNG: getPlayerById needs a proper implementation to find any player, not just activePlayer.");
        return null;
    }

}
