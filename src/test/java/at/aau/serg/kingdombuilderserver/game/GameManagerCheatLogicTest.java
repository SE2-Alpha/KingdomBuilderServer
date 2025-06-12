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
    void setUp(){
        player1 = new Player("p1", "Cheater");
        player1.setGold(10);
        player2 = new Player("p2", "Reporter");
        player2.setGold(10);
        player3 = new Player("p3", "Bystander");
        player3.setGold(10);

        List<Player> players = Arrays.asList(player1,player2,player3);

        gameManager = new GameManager(players);
        gameManager.setActivePlayer(player1);
        gameManager.setAwaitingCheatReports(true);
    }

    @Test
    void testPlayerCheatsAndIsCaught_HousesRemovedAndReporterGetsGold(){
        player1.setHasCheated(true);
        // Simuliere, dass der Spieler ein Haus gesetzt hat
        GameHousePosition cheatedHouse = new GameHousePosition(5,5);
        player1.getHousesPlacedThisTurn().add(cheatedHouse);
        player1.decreaseSettlementsBy(1);

        // Spieler 2 meldet den Cheat
        gameManager.recordCheatReport(player2.getId(), player1.getId());

        int initialReporterGold = player2.getGold();
        int initialCheaterSettlements=player1.getRemainingSettlements();

        gameManager.processCheatReportOutcome();

        assertEquals(initialReporterGold + 5, player2.getGold(), "Der Melder sollte 5 Gold erhalten.");
        assertEquals(10, player1.getGold(), "Der Schummler sollte kein Gold verlieren oder gewinnen.");
        assertTrue(player1.getHousesPlacedThisTurn().isEmpty(), "Die in dieser Runde gelegten Häuser des Schummlers sollten entfernt werden.");

        assertEquals(initialCheaterSettlements, player1.getRemainingSettlements(), "Siedlungen sollten (noch) nicht zurückgegeben werden, basierend auf dem Code.");
    }

    @Test
    void testPlayerCheatsAndGetsAwayWithIt_StateRemains(){
        player1.setHasCheated(true);
        GameHousePosition cheatedHouse = new GameHousePosition(5, 5);
        player1.getHousePlacedThisTurn().add(cheatedHouse);
        // Keine Meldung wird registriert (leere Reporterliste)

        gameManager.processCheatReportOutcome();

        assertEquals(10, player1.getGold(), "Gold des Schummlers sollte unverändert sein.");
        assertEquals(10, player2.getGold(), "Gold des anderen Spielers sollte unverändert sein.");
        assertFalse(player1.getHousesPlacedThisTurn().isEmpty(), "Das geschummelte Haus sollte (vor dem Cleanup) noch da sein.");
    }




}
