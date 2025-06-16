package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void undoLastMove_WhenHousesArePlaced_ShouldCallGameBoardAndClearList() {
        // Arrange
        // Füge platzierte Gebäude zur Sequenzliste hinzu
        List<Integer> placedHouses = new ArrayList<>(Arrays.asList(5, 10, 15));
        gameManager.setActiveBuildingsSequence(placedHouses);
        assertTrue(gameManager.getActiveBuildingsSequence().contains(5));

        // Act
        gameManager.undoLastMove(mockPlayer);

        // Assert
        // Überprüfe, ob die undoMove Methode auf dem GameBoard mit der korrekten Liste aufgerufen wurde
        verify(mockGameBoard, times(1)).undoMove(placedHouses, mockPlayer);
        // Überprüfe, ob die Liste der platzierten Gebäude im GameManager geleert wurde
        assertTrue(gameManager.getActiveBuildingsSequence().isEmpty(), "Active buildings sequence should be empty after undo.");
    }
}
