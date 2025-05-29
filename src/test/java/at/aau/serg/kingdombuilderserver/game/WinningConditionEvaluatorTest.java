package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

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
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        evaluator = new WinningConditionEvaluator(board, players);
    }

    @Test
    public void testConstructor() {

    }
}
