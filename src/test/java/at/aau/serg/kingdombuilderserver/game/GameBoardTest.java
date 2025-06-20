package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import at.aau.serg.kingdombuilderserver.board.TerrainType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    public void setUp() {
        board = new GameBoard();
        board.buildGameBoard();  // Spielfeld initialisieren
    }

    @Test
    public void testGetFieldTypeValidId() {
        // Beispiel: Feld mit ID 0 sollte einen gültigen TerrainType haben (z.B. nicht null)
        TerrainType type = board.getFieldType(0);
        assertNotNull(type, "TerrainType für Feld 0 sollte nicht null sein");
    }

    @Test
    public void testGetFieldTypeInvalidIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            board.getFieldType(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            board.getFieldType(400);
        });
    }
}
