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

    public void nextRound() {
        roundCounter++;
        activePlayer.setCurrentCard(null);
        activeBuildings.clear();
    }

    public void registerCheatReport (String reporterId){
        if (awaitingCheatReports){
            reports.add(reporterId);
        }
    }

    public void processCheatReportOutcome(){
        if (activePlayer == null) {
            System.err.println("processCheatReportOutcome called with no active players");
            return;
        }
        String activePlayerId = activePlayer.getId();
        boolean playerActuallyCheated = activePlayer.getHasCheated();
        List<String> reporters = cheatReportsThisTurn.getOrDefault(activePlayerId, new ArrayList<>());

        if (playerActuallyCheated){
            if(!reporters.isEmpty()){
                // Falls: Erfolgreich erwischt!
                System.err.println("Player " + activePlayerId + " was caught cheating by " + reporters.size() + " player(s).");
                TerrainField first = gameBoard.getFields()[activeBuildings.get(0)];
                gameBoard.remove(first,activeBuildings,activePlayer);

                // 2. Gold an die Entlarvenden übertragen
                for (String reporterId : reporters) {
                    Player reporter = getPlayerById(reporterId);
                    if (reporter != null) {
                        reporter.addGold(5);
                        System.out.println("Player " + reporterId + " receives 5 gold for reporting.");
                    }
                }
            } else {
                // Fall: Erfolgreich geschummelt (geschummelt, aber nicht erwischt)
                System.out.println("Player " + activePlayerId + " successfully cheated (was not reported).");
                // Häuser bleiben, kein Goldtransfer
            }
        } else { // Spieler hat NICHT geschummelt
            if (!reporters.isEmpty()) {
                // Fall: Fälschlicherweise beschuldigt
                System.out.println("Player " + activePlayerId + " was falsely accused by " + reporters.size() + " player(s).");
                for (String accuserId : reporters) {
                    Player accuser = getPlayerById(accuserId);
                    if (accuser != null) {
                        // 1. Beschuldigter bekommt Gold vom Anschuldigenden
                        int goldTransfer = Math.min(accuser.getGold(), 5); // Nicht mehr als der Ankläger hat
                        activePlayer.addGold(goldTransfer);
                        accuser.decreaseGold(goldTransfer);
                        System.out.println("Player " + activePlayerId + " receives " + goldTransfer + " gold from accuser " + accuserId);

                        // 2. Reporter setzt eine Runde aus
                        accuser.setSkippedTurn(true);
                        System.out.println("Player " + accuserId + " will skip next turn.");
                    }
                }
            } else {
                // Fall: Normaler Zug (nicht geschummelt, nicht gemeldet)
                System.out.println("Player " + activePlayerId + " completed turn normally (no cheating, no reports).");
            }
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
            System.err.println("Attempt to record cheat report outside of allowed window");
            return;
        }
        if (activePlayer != null && activePlayer.getId().equals(reportedPlayerId)){
            this.cheatReportsThisTurn.computeIfAbsent(reportedPlayerId, k -> new ArrayList<>()).add(reporterPlayerId);
        } else {
            System.err.println("Attempt to report non-active player or active player is null");
        }
    }

    public void cleanupTurn() {
        if (activePlayer != null){
            activePlayer.setHasCheated(false);
            cheatMode = false;
            activeBuildings.clear(); // Liste der in dieser Runde platzierten Häuser für den nächsten Zug zurücksetzen
        }
        this.cheatReportsThisTurn.clear(); // Auch hier die Reports löschen
        System.out.println("Turn cleanup for player" + (activePlayer != null ? activePlayer.getId() : "null"));
    }

    // Hilfmethode, um Spieler anhand der ID zu finden
    private Player getPlayerById(String playerId){
        if(playerId == null) return null;
        for (Player player : allPlayers){
            if(playerId.equals(player.getId())){
                return player;
            }
        }
        System.err.println("WARNUNG: Spieler mit ID " + playerId + " nicht gefunden.");
        return null;
    }

    private boolean isPositionValidForPlayer(Player player, GameHousePosition position) {
        if (player.getRemainingSettlements() <= 0) {
            System.err.println("Spieler hat keine Siedlungen mehr übrig.");
            return false;
        }
        return true;
    }

}
