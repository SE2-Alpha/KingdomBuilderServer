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
    private WinningConditionEvaluator evaluator2;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private List<Player> players;
    GameBoard board;
    GameBoard boardGrass;

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

        boardGrass = new GameBoard();
        boardGrass.buildGameBoard();

        for (int i = 0; i < boardGrass.getFields().length; i++) {
            boardGrass.getFields()[i] = new TerrainField(TerrainType.GRASS, i);
        }

        evaluator2 = new WinningConditionEvaluator(boardGrass, players);
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

        player1.setHouseFieldIds(Set.of());
        player2.setHouseFieldIds(Set.of());
        player3.setHouseFieldIds(Set.of());

        List<Player> winners = evaluator.evaluateWinner();

        assertEquals(1, winners.size());
        assertTrue(winners.contains(player4));
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
    void testEvaluateMiners_OneHOuseMultipleMountains() {
        // Setup Spielfeld mit Bergen neben Häusern
        TerrainField[] fields = board.getFields();
        // Beispiel: Haus auf Feld 21, Berge auf 20 und 22
        player1.setHouseFieldIds(Set.of(21));
        fields[20].setType(TerrainType.MOUNTAIN);
        fields[22].setType(TerrainType.MOUNTAIN);

        evaluator = new WinningConditionEvaluator(board, players);
        int points = evaluator.evaluateMiners(player1);

        // Erwartet: Nur 1 Punkt, auch wenn mehrere Berge angrenzen
        assertEquals(1, points);
    }

    @Test
    void testEvaluateMiners_MultipleHousesOneMountain() {
        TerrainField[] fields = board.getFields();

        // Häuser auf beiden Seiten eines Berges
        player1.setHouseFieldIds(Set.of(20, 22));
        fields[21].setType(TerrainType.MOUNTAIN);

        evaluator = new WinningConditionEvaluator(board, players);

        // Beide Häuser grenzen an denselben Berg => 2 Punkte -> ein Punkt pro Siedlung
        assertEquals(2, evaluator.evaluateMiners(player1));
    }

    @Test
    void testEvaluateMiners_MultipleHousesDifferentMountains() {
        TerrainField[] fields = board.getFields();

        // Zwei Häuser bei zwei verschiedenen Bergen
        player1.setHouseFieldIds(Set.of(30, 40));
        fields[29].setType(TerrainType.MOUNTAIN);
        fields[41].setType(TerrainType.MOUNTAIN);

        evaluator = new WinningConditionEvaluator(board, players);

        // Zwei verschiedene Berge angrenzend => 2 Punkte
        assertEquals(2, evaluator.evaluateMiners(player1));
    }

    @Test
    void testEvaluateDiscoverers() {
        int points1 = evaluator.evaluateDiscoverers(player1);
        int points3 = evaluator.evaluateDiscoverers(player3);
        assertEquals(4, points1);
        assertEquals(10, points3);
    }

    @Test
    void testEvaluateDiscoverers_Empty() {
        player1.setHouseFieldIds(Set.of());
        evaluator = new WinningConditionEvaluator(board, players);

        assertEquals(0, evaluator.evaluateDiscoverers(player1));
    }

    @Test
    void testEvaluateDiscoverers_AllRows() {
        Set<Integer> allRowFields = new HashSet<>();
        for (int i = 0; i < board.getFields().length; i += 30) {
            allRowFields.add(i); // z.B. 0, 30, 60, ..., falls 30 Spalten
        }
        player1.setHouseFieldIds(allRowFields);

        evaluator = new WinningConditionEvaluator(board, players);

        // Anzahl unterschiedlicher Y-Koordinaten
        assertEquals(allRowFields.size(), evaluator.evaluateDiscoverers(player1));
    }

    @Test
    void testEvaluateCastleFields() {
        TerrainField[] fields = boardGrass.getFields();

        fields[100].setType(TerrainType.CITY); // CITY-Feld
        player1.setHouseFieldIds(Set.of(101)); // Direkt angrenzendes Haus

        evaluator2 = new WinningConditionEvaluator(boardGrass, players);
        int points = evaluator2.evaluateCastleFields(player1);

        assertEquals(3, points); // 1 Burg angrenzend => 3 Punkte
    }

    @Test
    void testEvaluateCastleFields_MultipleAdjacent() {
        TerrainField[] fields = boardGrass.getFields();

        fields[100].setType(TerrainType.CITY);
        fields[200].setType(TerrainType.CITY);
        player1.setHouseFieldIds(Set.of(101, 201));

        evaluator2 = new WinningConditionEvaluator(boardGrass, players);
        int points = evaluator2.evaluateCastleFields(player1);

        assertEquals(6, points); // 2 Burgen => 6 Punkte
    }

    @Test
    void testEvaluateCastleFields_NotAdjacent() {
        TerrainField[] fields = boardGrass.getFields();

        fields[100].setType(TerrainType.CITY);
        player1.setHouseFieldIds(Set.of(150)); // Kein Nachbar

        evaluator2 = new WinningConditionEvaluator(boardGrass, players);
        int points = evaluator2.evaluateCastleFields(player1);

        assertEquals(0, points); // Kein angrenzendes Haus
    }

    @Test
    void testEvaluateCastleFields_HouseSurroundedByMultipleCastles() {
        TerrainField[] fields = boardGrass.getFields();

        fields[120].setType(TerrainType.CITY);
        fields[122].setType(TerrainType.CITY);
        player1.setHouseFieldIds(Set.of(121)); // Dazwischen

        evaluator2 = new WinningConditionEvaluator(boardGrass, players);

        assertEquals(6, evaluator2.evaluateCastleFields(player1)); // Beide Burgen angrenzend
    }

    @Test
    void testEvaluateCastleFields_MultipleHousesSameCastle() {
        TerrainField[] fields = boardGrass.getFields();

        fields[100].setType(TerrainType.CITY);
        player1.setHouseFieldIds(Set.of(99, 101)); // Beide an gleiche Burg

        evaluator2 = new WinningConditionEvaluator(boardGrass, players);

        assertEquals(3, evaluator2.evaluateCastleFields(player1)); // Nur einmal zählen
    }

    @Test
    void testEvaluateCastleFields_HousesNearDifferentCastles() {
        TerrainField[] fields = boardGrass.getFields();

        fields[50].setType(TerrainType.CITY);
        fields[150].setType(TerrainType.CITY);
        player1.setHouseFieldIds(Set.of(49, 151));

        evaluator2 = new WinningConditionEvaluator(boardGrass, players);

        assertEquals(6, evaluator2.evaluateCastleFields(player1)); // 2 Burgen angrenzend
    }


}
