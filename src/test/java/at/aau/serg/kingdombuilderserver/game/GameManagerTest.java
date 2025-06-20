package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
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

        List<Player> mockPlayerList = new ArrayList<>();

        mockPlayerList.add(mockPlayer);

        // Testfreundlichen Konstruktor verwenden
        gameManager = new GameManager(mockGameBoard, mockPlayerList);
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
    void testPlaceHouse_PlayerHasNoSettlements() {

        when(mockPlayer.getRemainingSettlements()).thenReturn(0);
        GameHousePosition position = new GameHousePosition(1, 1);
        when(mockGameBoard.isPositionValid(position)).thenReturn(true);

        gameManager.placeHouse(position);

        verify(mockGameBoard, times(1)).placeHouse(any(), any(), any(), anyInt());
        verify(mockPlayer, never()).decreaseSettlementsBy(1);
        assertTrue(mockPlayer.getHousesPlacedThisTurn().isEmpty());
    }

    @Test
    void testPlaceHouse_noActivePlayer() {
        gameManager.setActivePlayer(null);
        GameHousePosition position = new GameHousePosition(1, 1);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> gameManager.placeHouse(position));
        assertEquals("Kein aktiver Spieler ausgewählt, um ein Haus zu platzieren.", ex.getMessage());
    }

    @Test
    void testRecordCheatReport_WhenNotAwaiting() {
        gameManager.setAwaitingCheatReports(false);
        gameManager.recordCheatReport("reporterId", "reportedId");
        assertTrue(gameManager.getCheatReportsThisTurn().isEmpty());
    }

    @Test
    void testPublicConstructor() {
        // Testet den öffentlichen Konstruktor, der in der echten Anwendung verwendet wird
        List<Player> players = new ArrayList<>();
        players.add(new Player("p1", "Tester"));
        GameManager realGameManager = new GameManager(players);

        // Die wichtigste Zusicherung ist, dass das interne GameBoard-Objekt erstellt wird
        assertNotNull(realGameManager.getGameBoard());
    }

    @Test
    void testNextRound_WithActivePlayer() {
        // Stellt sicher, dass die Liste der in dieser Runde platzierten Häuser für den Spieler geleert wird
        when(mockPlayer.getHousesPlacedThisTurn()).thenReturn(new ArrayList<>());
        gameManager.nextRound();
        verify(mockPlayer).getHousesPlacedThisTurn();
    }

    @Test
    void testNextRound_WithNullActivePlayer() {
        // Stellt sicher, dass kein NullPointerException auftritt, wenn kein Spieler aktiv ist
        gameManager.setActivePlayer(null);
        assertDoesNotThrow(() -> gameManager.nextRound());
        // Der Zähler sollte trotzdem erhöht werden
        assertEquals(1, gameManager.getRoundCounter());
    }

    @Test
    void testRegisterCheatReport_WhenAwaiting() {
        gameManager.setAwaitingCheatReports(true);
        gameManager.registerCheatReport("reporter1");

        assertDoesNotThrow(() -> gameManager.registerCheatReport("reporter1"));
    }

    @Test
    void testRegisterCheatReport_WhenNotAwaiting() {
        gameManager.setAwaitingCheatReports(false);
        gameManager.registerCheatReport("reporter1");

        // Ähnlich wie oben, wir stellen sicher, dass der Code ausgeführt wird, ohne den Zustand zu ändern.
        assertDoesNotThrow(() -> gameManager.registerCheatReport("reporter1"));
    }

    @Test
    void testSetAwaitingCheatReports_ClearsOldReports() {
        // Füge einen alten Report hinzu
        gameManager.getCheatReportsThisTurn().put("p1", List.of("p2"));
        assertFalse(gameManager.getCheatReportsThisTurn().isEmpty());

        // Das Setzen auf 'true' sollte die Liste leeren
        gameManager.setAwaitingCheatReports(true);
        assertTrue(gameManager.getCheatReportsThisTurn().isEmpty());
    }

    @Test
    void testCleanupTurn_WithNullActivePlayer() {
        // Stellt sicher, dass kein NullPointerException auftritt
        gameManager.setActivePlayer(null);
        assertDoesNotThrow(() -> gameManager.cleanupTurn());
    }

    @Test
    void testPlaceHouse_WhenPlayerHasSettlements() {
        GameHousePosition validPosition = new GameHousePosition(3, 3);
        when(mockGameBoard.isPositionValid(validPosition)).thenReturn(true);

        when(mockPlayer.getRemainingSettlements()).thenReturn(10);

        gameManager.placeHouse(validPosition);

        verify(mockPlayer, times(1)).decreaseSettlementsBy(1);
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

    @Test
    void undoLastMove_WhenNoHousesArePlaced_ShouldNotCallGameBoard() {
        // Arrange
        // Die Liste ist bereits leer nach dem Setup
        assertTrue(gameManager.getActiveBuildingsSequence().isEmpty());

        // Act
        gameManager.undoLastMove(mockPlayer);

        // Assert
        // Überprüfe, ob die undoMove Methode des GameBoards NIEMALS aufgerufen wurde
        verify(mockGameBoard, never()).undoMove(any(), any());
    }
}
