package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class GameManagerCheatLogicTest {

    private GameManager gameManager;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        player1 = new Player("p1", "Cheater");
        player1.setCheatPoints(10);
        player2 = new Player("p2", "Reporter");
        player2.setCheatPoints(10);
        player3 = new Player("p3", "Bystander");
        player3.setCheatPoints(10);

        List<Player> players = Arrays.asList(player1, player2, player3);

        gameManager = new GameManager(players);
        gameManager.setActivePlayer(player1);
        gameManager.setAwaitingCheatReports(true);
    }

    @Test
    void testPlayerCheatsAndIsCaught_HousesRemovedAndReporterGetsGold() {
        player1.setHasCheated(true);
        gameManager.setActivePlayer(player1);
        // Simuliere, dass der Spieler ein Haus gesetzt hat
        GameHousePosition cheatedHouse = new GameHousePosition(5, 5);
        gameManager.getActiveBuildings().add(cheatedHouse.toId());
        player1.decreaseSettlementsBy(1);

        // Spieler 2 meldet den Cheat
        gameManager.recordCheatReport(player2.getId(), player1.getId());

        int initialReporterGold = player2.getCheatPoints();
        int initialCheaterSettlements = player1.getRemainingSettlements();

        gameManager.processCheatReportOutcome();

        assertEquals(initialReporterGold + 5, player2.getCheatPoints(), "Der Melder sollte 5 Gold erhalten.");
        assertEquals(10, player1.getCheatPoints(), "Der Schummler sollte kein Gold verlieren oder gewinnen.");
        assertTrue(gameManager.getActiveBuildings().isEmpty(), "Die in dieser Runde gelegten Häuser des Schummlers sollten entfernt werden.");

        assertEquals(initialCheaterSettlements, player1.getRemainingSettlements(), "Siedlungen sollten (noch) nicht zurückgegeben werden, basierend auf dem Code.");
    }

    @Test
    void testPlayerCheatsAndGetsAwayWithIt_StateRemains() {
        player1.setHasCheated(true);
        GameHousePosition cheatedHouse = new GameHousePosition(5, 5);
        int ID = cheatedHouse.getY() * 20 + cheatedHouse.getX();
        gameManager.getActiveBuildings().add(ID);
        // Keine Meldung wird registriert (leere Reporterliste)

        gameManager.processCheatReportOutcome();

        assertEquals(10, player1.getCheatPoints(), "Gold des Schummlers sollte unverändert sein.");
        assertEquals(10, player2.getCheatPoints(), "Gold des anderen Spielers sollte unverändert sein.");
        assertFalse(gameManager.getActiveBuildings().isEmpty(), "Das geschummelte Haus sollte (vor dem Cleanup) noch da sein.");
    }

    @Test
    void testPlayerIsFalseAccused_AccusedGetsGoldAndAccuserSkipsTurn() {
        player1.setHasCheated(false); // Der Spieler hat NICHT geschummelt

        // Spieler 2 beschuldigt Spieler 1 fälschlicherweise
        gameManager.recordCheatReport(player2.getId(), player1.getId());

        int initialAccusedGold = player1.getCheatPoints();
        int initialAccuserGold = player2.getCheatPoints();

        gameManager.processCheatReportOutcome();

        assertEquals(initialAccusedGold + 5, player1.getCheatPoints(), "Der zu Unrecht Beschuldigte sollte 5 Gold erhalten.");
        assertEquals(initialAccuserGold - 5, player2.getCheatPoints(), "Der Ankläger sollte 5 Gold verlieren.");
        assertTrue(player2.isSkippedTurn(), "Der Ankläger sollte die nächste Runde aussetzen.");
        assertFalse(player1.isSkippedTurn(), "Der zu Unrecht Beschuldigte sollte nicht aussetzen.");
    }

    @Test
    void testPlayerIsFalselyAccused_AccuserHasNotEnoughGold() {
        player1.setHasCheated(false);
        player2.setCheatPoints(3); // Ankläger hat nur 3 Gold

        gameManager.recordCheatReport(player2.getId(), player1.getId());

        gameManager.processCheatReportOutcome();

        assertEquals(10 + 3, player1.getCheatPoints(), "Der Beschuldigte sollte nur das verfügbare Gold (3) erhalten.");
        assertEquals(0, player2.getCheatPoints(), "Der Ankläger sollte all sein Gold verlieren.");
        assertTrue(player2.isSkippedTurn(), "Der Ankläger sollte trotzdem eine Runde aussetzen.");
    }

    @Test
    void testNormalTurnWithoutCheatingOrReports_NoStateChanges() {
        player1.setHasCheated(false);

        gameManager.processCheatReportOutcome();

        assertEquals(10, player1.getCheatPoints(), "Gold von Spieler 1 sollte unverändert sein.");
        assertEquals(10, player2.getCheatPoints(), "Gold von Spieler 2 sollte unverändert sein.");
        assertFalse(player1.isSkippedTurn(), "Spieler 1");
        assertFalse(player2.isSkippedTurn(), "Spieler 2");
    }

    @Test
    void testCleanupTurn_ResetsPlayerAndGameState() {
        player1.setHasCheated(true);
        gameManager.setActivePlayer(player1);
        GameHousePosition pos = new GameHousePosition(1, 1);
        gameManager.getActiveBuildings().add(pos.toId());
        gameManager.recordCheatReport(player2.getId(), player1.getId());

        gameManager.cleanupTurn();

        assertFalse(player1.getHasCheated(), "Der hasCheated-Flag des Spielers sollte zurückgesetzt werden.");
        assertTrue(gameManager.getActiveBuildings().isEmpty(), "Die Liste der gelegten Häuser sollte geleert werden.");
        assertTrue(gameManager.getCheatReportsThisTurn().isEmpty(), "Die Cheat-Meldungen für die Runde sollten gelöscht werden.");
    }


    @Test
    void testRecordCheatReport_OnlyRecordsWhenAwaitingReports() {
        gameManager.setAwaitingCheatReports(false); // Das Fenster ist geschlossen
        gameManager.recordCheatReport(player2.getId(), player1.getId());

        assertTrue(gameManager.getCheatReportsThisTurn().getOrDefault(player1.getId(), List.of()).isEmpty(), "Es sollte kein Report registriert werden, wenn das Fenster geschlossen ist.");

        gameManager.setAwaitingCheatReports(true);
        gameManager.recordCheatReport(player2.getId(),player1.getId());

        assertFalse(gameManager.getCheatReportsThisTurn().get(player1.getId()).isEmpty(), "Der Report sollte registriert werden, wenn das Fenster offen ist.");

    }

    @Test
    void testPlayerCheatsAndIsCaughtByMultipleReporters_AllReportersGetGold() {
        player1.setHasCheated(true);
        gameManager.getActiveBuildings().add(new GameHousePosition(1, 1).toId());

        // Spieler 2 und Spieler 3 melden den Cheat
        gameManager.recordCheatReport(player2.getId(), player1.getId());
        gameManager.recordCheatReport(player3.getId(), player1.getId());

        int initialGoldP2 = player2.getCheatPoints();
        int initialGoldP3 = player3.getCheatPoints();

        gameManager.processCheatReportOutcome();

        assertEquals(initialGoldP2 + 5, player2.getCheatPoints(), "Spieler 2 sollte 5 Gold als Belohnung erhalten.");
        assertEquals(initialGoldP3 + 5, player3.getCheatPoints(), "Spieler 3 sollte ebenfalls 5 Gold erhalten.");
        assertTrue(gameManager.getActiveBuildings().isEmpty(), "Die Häuser des Schummlers sollten nur einmal entfernt werden.");
        assertEquals(10, player1.getCheatPoints(), "Der Schummler sollte kein Gold verlieren.");
    }

    @Test
    void testPlayerIsFalselyAccusedByMultiplePlayers_AccusedGetsGoldFromAll() {
        player1.setHasCheated(false); // Nicht geschummelt

        // Spieler 2 und 3 beschuldigen Spieler 1
        gameManager.recordCheatReport(player2.getId(), player1.getId());
        gameManager.recordCheatReport(player3.getId(), player1.getId());

        int initialGoldP1 = player1.getCheatPoints();
        int initialGoldP2 = player2.getCheatPoints();
        int initialGoldP3 = player3.getCheatPoints();

        gameManager.processCheatReportOutcome();

        assertEquals(initialGoldP1 + 10, player1.getCheatPoints(), "Der Beschuldigte sollte von beiden Anklägern je 5 Gold erhalten.");
        assertEquals(initialGoldP2 - 5, player2.getCheatPoints(), "Ankläger 1 (Spieler 2) sollte 5 Gold verlieren.");
        assertEquals(initialGoldP3 - 5, player3.getCheatPoints(), "Ankläger 2 (Spieler 3) sollte 5 Gold verlieren.");
        assertTrue(player2.isSkippedTurn(), "Ankläger 1 muss eine Runde aussetzen.");
        assertTrue(player3.isSkippedTurn(), "Ankläger 2 muss ebenfalls eine Runde aussetzen.");
    }

    @Test
    void testRecordCheatReport_IgnoresReportAgainstNonActivePlayer(){
        // player1 ist der aktive Spieler. Spieler 3 meldet Spieler 2
        gameManager.recordCheatReport(player3.getId(), player2.getId());

        gameManager.processCheatReportOutcome(); // Dies wird für player1 ausgeführt

        // Es sollte absolut nichts passieren, was die Spieler betrifft
        assertEquals(10, player1.getCheatPoints());
        assertEquals(10, player2.getCheatPoints());
        assertEquals(10, player3.getCheatPoints());
        assertFalse(player1.isSkippedTurn());
        assertFalse(player2.isSkippedTurn());
        assertFalse(player3.isSkippedTurn());

        // Wichtig: Die Report-Liste für player1 sollte leer sein
        assertTrue(gameManager.getCheatReportsThisTurn().getOrDefault(player1.getId(), List.of()).isEmpty());
    }

    @Test
    void testNextPlayerLogic_CorrectlySkipsPlayerMarkedToSkip(){
        Room room = new Room("roomId", "Test Room");
        Player p1 = new Player("p1", "Alice");
        Player p2 = new Player("p2", "Bob");
        Player p3 = new Player("p3", "Charlie");
        room.addPlayer(p1);
        room.addPlayer(p2);
        room.addPlayer(p3);

        p2.setSkippedTurn(true); // Bob soll diese Runde aussetzen

        // Aktueller Spieler ist p1
        Player activePlayer = p1;

        Player nextPlayer = room.getNextPlayer(activePlayer);
        if (nextPlayer != null && nextPlayer.isSkippedTurn()){
            nextPlayer.setSkippedTurn(false);
            nextPlayer = room.getNextPlayer(nextPlayer);
        }

        assertNotNull(nextPlayer);
        assertEquals(p3.getId(), nextPlayer.getId(), "Der nächst Spieler sollte p3 sein, da p2 übersprungen wurde.");
        assertFalse(p2.isSkippedTurn(), "Das skip-Flag von p2 sollte für die Zukunft zurückgesetzt worden sein.");
    }

    @Test
    void testProcessCheatReportOutcome_WithNullActivePlayer() {
        gameManager.setActivePlayer(null);

        assertDoesNotThrow(() -> gameManager.processCheatReportOutcome());
    }
    @Test
    void testProcessCheatReportOutcome_FalselyAccusedByNonExistentPlayer() {
        player1.setHasCheated(false);
        int initialGold = player1.getCheatPoints();

        gameManager.recordCheatReport("non-existent-accuser", player1.getId());

        gameManager.processCheatReportOutcome();

        assertEquals(initialGold, player1.getCheatPoints(), "Gold sollte sich nicht ändern, wenn der Ankläger nicht existiert.");
    }
}
