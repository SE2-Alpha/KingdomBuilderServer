package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import at.aau.serg.kingdombuilderserver.board.TerrainField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import java.util.*;


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
    private List<Integer> activeBuildings = new ArrayList<>();
    @Getter
    private boolean awaitingCheatReports = false;
    private final List<Player> allPlayers;
    private Set<String> reports = new HashSet<>();
    @Getter
    @Setter
    private Map<String, List<String>> cheatReportsThisTurn;
    @Getter
    @Setter
    private boolean cheatMode;

    public GameManager(List<Player> players) {
        // Private constructor to prevent instantiation
        this.gameBoard = new GameBoard();
        gameBoard.buildGameBoard();
        this.allPlayers = players;
        this.cheatReportsThisTurn = new HashMap<>();
        this.cheatMode = false;
    }

    // Zusätzlicher Konstruktor NUR FÜR TESTS (package-private)
    GameManager(GameBoard gameBoard, List<Player> allPlayers) {
        this.gameBoard = gameBoard;
        this.allPlayers = allPlayers;
        this.cheatReportsThisTurn = new HashMap<>();
    }

    public void placeHouse(GameHousePosition position) {
        gameBoard.placeHouse(activePlayer,activeBuildings,position,cheatMode,roundCounter);
    }

    /**
     * @return new cheatMode (boolean)
     */
    public boolean toggleCheatMode(){
        cheatMode = !cheatMode;
        return cheatMode;
    }

    public void undoLastMove(Player player){
        if (!activeBuildings.isEmpty()){
            gameBoard.undoMove(activeBuildings, player);
            activeBuildings.clear();
        }
    }

    public void nextRound() {
        roundCounter++;
    }

    public void registerCheatReport (String reporterId){
        if (awaitingCheatReports){
            reports.add(reporterId);
        }
    }

    public void processCheatReportOutcome(){
        if (activePlayer == null) {
            return;
        }
        String activePlayerId = activePlayer.getId();
        boolean playerActuallyCheated = activePlayer.getHasCheated();
        List<String> reporters = cheatReportsThisTurn.getOrDefault(activePlayerId, new ArrayList<>());

        if (playerActuallyCheated){
            if(!reporters.isEmpty()){
                // Falls: Erfolgreich erwischt!
                TerrainField first = gameBoard.getFields()[activeBuildings.get(0)];
                gameBoard.remove(first,activeBuildings,activePlayer);

                // 2. Gold an die Entlarvenden übertragen
                for (String reporterId : reporters) {
                    Player reporter = getPlayerById(reporterId);
                    if (reporter != null) {
                        reporter.addCheatPoints(5);
                    }
                }
            }// Fall: Spieler wurde nicht erwischt, keine Strafe
        } else { // Spieler hat NICHT geschummelt
            if (!reporters.isEmpty()) {
                // Fall: Fälschlicherweise beschuldigt
                for (String accuserId : reporters) {
                    Player accuser = getPlayerById(accuserId);
                    if (accuser != null) {
                        // 1. Beschuldigter bekommt Gold vom Anschuldigenden
                        int goldTransfer = Math.min(accuser.getCheatPoints(), 5); // Nicht mehr als der Ankläger hat
                        activePlayer.addCheatPoints(goldTransfer);
                        accuser.decreaseCheatPoints(goldTransfer);

                        // 2. Reporter setzt eine Runde aus
                        accuser.setSkippedTurn(true);
                    }
                }
            } // Fall: Normaler Zug (nicht geschummelt, nicht gemeldet)
        }
        // Wichtig: hasCheated für den nächsten Zug zurücksetzen, geschieht in cleanupTurn
    }
    public void setAwaitingCheatReports(boolean awaitingCheatReports){
        this.awaitingCheatReports = awaitingCheatReports;
        if (awaitingCheatReports){
            this.cheatReportsThisTurn.clear();  // Alte Meldungen für neue Runde löschen
        }
    }

    public void recordCheatReport(String reporterPlayerId, String reportedPlayerId){
        if(!awaitingCheatReports){
            return;
        }
        if (activePlayer != null && activePlayer.getId().equals(reportedPlayerId)){
            this.cheatReportsThisTurn.computeIfAbsent(reportedPlayerId, k -> new ArrayList<>()).add(reporterPlayerId);
        }
    }

    public void cleanupTurn() {
        if (activePlayer != null){
            activePlayer.setHasCheated(false);
            cheatMode = false;
            activeBuildings.clear(); // Liste der in dieser Runde platzierten Häuser für den nächsten Zug zurücksetzen
            activePlayer.setCurrentCard(null);
        }
        this.cheatReportsThisTurn.clear(); // Auch hier die Reports löschen
    }

    // Hilfmethode, um Spieler anhand der ID zu finden
    private Player getPlayerById(String playerId){
        if(playerId == null) return null;
        for (Player player : allPlayers){
            if(playerId.equals(player.getId())){
                return player;
            }
        }
        return null;
    }


}
