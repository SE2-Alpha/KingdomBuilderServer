package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WinningConditionEvaluatorTest {

    private WinningConditionEvaluator evaluator;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private List<Player> players;
    GameBoardWinningConditions board;

    @BeforeEach
    void setUp() {
        board = new GameBoardWinningConditions();
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
    void testEvaluateDiscoverers() {
        int points = evaluator.evaluateDiscoverers(player1);
        assertEquals(4, points);
    }
    
}
