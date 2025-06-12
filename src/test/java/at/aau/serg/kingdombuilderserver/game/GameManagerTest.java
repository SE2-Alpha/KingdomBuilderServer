package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GameManagerTest {

    private GameManager gameManager;
    private GameBoard mockGameBoard;
    private Player mockPlayer;
    private List<Integer> fieldBuffer;

    @BeforeEach
    void setUp() {
        // Mock-Objekte erzeugen
        mockGameBoard = mock(GameBoard.class);
        mockPlayer = mock(Player.class);

        List<Player> mockPlayerList = new ArrayList<>();

        mockPlayerList.add(mockPlayer);

        // Testfreundlichen Konstruktor verwenden
        gameManager = new GameManager(mockGameBoard);
        fieldBuffer = new ArrayList<>();
        // Aktiven Spieler setzen
        gameManager.setActivePlayer(mockPlayer);

        //Liste platzierter Gebäude setzen
        gameManager.setActiveBuildingsSequence(fieldBuffer);
    }

    @Test
    void testPlaceHouse_GueltigePosition() {
        GameHousePosition position = new GameHousePosition(2, 4);
        when(mockGameBoard.isPositionValid(position)).thenReturn(true);

        gameManager.placeHouse(position);

        // Überprüfen, ob placeHouse korrekt aufgerufen wurde
        verify(mockGameBoard, times(1)).placeHouse(mockPlayer, fieldBuffer, position, 0);
    }

    @Test
    void testPlaceHouse_UngueltigePosition() {
        GameHousePosition position = new GameHousePosition(2, 4);
        when(mockGameBoard.isPositionValid(position)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> gameManager.placeHouse(position));
        assertEquals("Ungültige Position für das Platzieren des Hauses: " + position, ex.getMessage());

        // Sicherstellen, dass placeHouse NICHT aufgerufen wurde
        verify(mockGameBoard, never()).placeHouse(any(),any(), any(), anyInt());
    }

    @Test
    void testNextRound() {
        assertEquals(0, gameManager.getRoundCounter());
        gameManager.nextRound();
        assertEquals(1, gameManager.getRoundCounter());
        gameManager.nextRound();
        assertEquals(2, gameManager.getRoundCounter());
    }
}
