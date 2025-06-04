package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import at.aau.serg.kingdombuilderserver.board.TerrainField;
import at.aau.serg.kingdombuilderserver.board.TerrainType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WinningConditionEvaluatorTest {

    private WinningConditionEvaluator evaluator;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private List<Player> players;
    GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard();
        board.buildGameBoard();
        player1 = new Player("1", "Player1");
        player2 = new Player("2", "Player2");
        player3 = new Player("3", "Player3");
        player4 = new Player("4", "Player4");

        players = new ArrayList<>(Arrays.asList(player1, player2, player3, player4));

        player1.setHouseFieldIds(Set.of(0, 1, 2, 3, 4, 5, 20, 40, 100));
        player2.setHouseFieldIds(Set.of(7, 9, 28, 46, 65, 90, 103, 141, 199, 203));
        player3.setHouseFieldIds(Set.of(14, 36, 48, 74, 91, 107, 122, 177, 205, 221));
        player4.setHouseFieldIds(Set.of(6, 29, 35, 60, 119, 130, 162, 188, 240, 301));

        evaluator = new WinningConditionEvaluator(board, players);
    }

    @Test
    void testEvaluateWinnersMultiple() {

        players.remove(player1);
        players.remove(player3);

        Set<Integer> sharedFields = Set.of(7, 9, 28, 46, 65, 90, 103, 141, 199, 203);
        player2.setHouseFieldIds(sharedFields);
        player4.setHouseFieldIds(sharedFields);

        List<Player> winners = evaluator.evaluateWinner();

        assertEquals(2, winners.size());
        assertTrue(winners.contains(player2));
        assertTrue(winners.contains(player4));
    }

    @Test
    void testEvaluateWinnersSingle() {
        List<Player> winners = evaluator.evaluateWinner();

        assertEquals(1, winners.size());
        assertTrue(winners.contains(player3));
    }

    @Test
    void testEvaluateDiscoverers() {
        int points1 = evaluator.evaluateDiscoverers(player1);
        int points3 = evaluator.evaluateDiscoverers(player3);
        assertEquals(4, points1);
        assertEquals(10, points3);
    }

    @Test
    void testEvaluateHermits_SingleFieldGroup() {
        // Nur 1 Haus = 1 Gruppe
        player1.setHouseFieldIds(Set.of(10));
        evaluator = new WinningConditionEvaluator(board, players);

        assertEquals(1, evaluator.evaluateHermits(player1));
    }

    @Test
    void testEvaluateHermits_MultipleFieldGroups() {
        // Spieler mit 2 getrennten Gruppen: {0, 1, 2}, {100}
        player1.setHouseFieldIds(Set.of(0, 1, 2, 100));
        evaluator = new WinningConditionEvaluator(board, players);

        int points = evaluator.evaluateHermits(player1);
        // Erwartet: 2 Gruppen => 2 Punkte
        assertEquals(2, points);
    }

    @Test
    void testEvaluateHermits_MultipleIsolatedFields() {
        // Keine Nachbarn, alle Felder einzeln
        player1.setHouseFieldIds(Set.of(1, 10, 100, 200));
        evaluator = new WinningConditionEvaluator(board, players);

        // 4 einzelne Gruppen
        assertEquals(4, evaluator.evaluateHermits(player1));
    }

    @Test
    void testEvaluateHermits_LargeConnectedGroup() {
        // Zusammenhängende Felder (z. B. horizontal)
        player1.setHouseFieldIds(Set.of(10, 11, 12, 13, 14));
        evaluator = new WinningConditionEvaluator(board, players);

        // Nur 1 große Gruppe
        assertEquals(1, evaluator.evaluateHermits(player1));
    }

    @Test
    void testEvaluateMiners() {}

    @Test
    void testEvaluateCastleFields(){}
}
