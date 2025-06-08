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
    private final List<Player> allPlayers;
    private Set<String> reports = new HashSet<>();
    private Map<String, List<String>> cheatReportsThisTurn;

    public GameManager(List<Player> players) {
        // Private constructor to prevent instantiation
        this.gameBoard = new GameBoard();
        gameBoard.buildGameBoard();
        this.allPlayers = players;
        this.cheatReportsThisTurn = new HashMap<>();
    }

    // Zusätzlicher Konstruktor NUR FÜR TESTS (package-private)
    GameManager(GameBoard gameBoard, List<Player> allPlayers) {
        this.gameBoard = gameBoard;
        this.allPlayers = allPlayers;
        this.cheatReportsThisTurn = new HashMap<>();
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
        if (activePlayer == null) {
            System.err.println("processCheatReportOutcome called with no active players");
            return;
        }
        String activePlayerId = activePlayer.getId();
        boolean playerActuallyCheated = activePlayer.hasCheated();
        List<String> reporters = cheatReportsThisTurn.getOrDefault(activePlayerId, new ArrayList<>());

        if (playerActuallyCheated){
            if(!reporters.isEmpty()){
                // Falls: Erfolgreich erwischt!
                System.err.println("Player " + activePlayerId + " was caught cheating by " + reporters.size() + " player(s).");

                // 1. Alle Häuser dieser Runde entfernen
                for (GameHousePosition pos: activePlayer.getHousesPlacedThisTurn()){
                    // gameBoard.removeHouse(pos);
                    System.out.println("Removing cheated house at " + pos + " for player " + activePlayerId);
                }
                activePlayer.getHousesPlacedThisTurn().clear(); // Liste der Häuser leeren
                activePlayer.decreaseSettlementsBy(0); // Später ändern zu increaseSettlementsBy(anzahlEntfernterHäuser)

                // 2. Gold an die Entlarvenden übertragen
                for (String reporterId : reporters) {
                    Player reporter = getPlayerById(reporterId);
                    if (reporter != null) {
                        reporter.setGold(reporter.getGold() + 5);
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
                        activePlayer.setGold(activePlayer.getGold() + goldTransfer);
                        accuser.setGold(accuser.getGold() - goldTransfer);
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
            activePlayer.getHousesPlacedThisTurn().clear(); // Liste der in dieser Runde platzierten Häuser für den nächsten Zug zurücksetzen
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
