package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.CheatReportDTO;
import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

class GameControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private Room room;

    @Mock
    private GameManager gameManager;

    @Mock
    private Player activePlayer;

    @InjectMocks
    private GameController gameController;

    private final String gameId = "game1";
    private final String playerId = "player1";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        RoomList.list.clear();
        RoomList.list.put(gameId, room);

        // Konfiguriere das Mock-Verhalten
        when(room.getGameManager()).thenReturn(gameManager);
        lenient().when(gameManager.getActivePlayer()).thenReturn(activePlayer);
        lenient().when(activePlayer.getId()).thenReturn(playerId);
        lenient().when(room.getId()).thenReturn(gameId);
    }

    @Test
    void handleCheat_GameNotFound() {
        // Arrange
        PlayerActionDTO cheatAction = new PlayerActionDTO();
        cheatAction.setGameId("nonExistentGame");

        // Act
        gameController.handleCheat(cheatAction);

        // Assert
        verify(gameManager, never()).placeHouse(any());
    }

    @Test
    void reportCheat_GameNotFound() {
        // Arrange
        CheatReportDTO report = new CheatReportDTO();
        report.setGameId("nonExistentGame");

        // Act
        gameController.reportCheat(report);

        // Assert
        verify(gameManager, never()).recordCheatReport(anyString(), anyString());
    }

    @Test
    void reportCheat_NotAwaitingReports() {
        // Arrange
        when(gameManager.isAwaitingCheatReports()).thenReturn(false);
        CheatReportDTO report = new CheatReportDTO();
        report.setGameId(gameId);

        // Act
        gameController.reportCheat(report);

        // Assert
        verify(gameManager, never()).recordCheatReport(anyString(), anyString());
    }


    @Test
    void undoLastMove_WhenPlayerIsActive_ShouldUndoAndBroadcast() {
        // Arrange
        PlayerActionDTO action = new PlayerActionDTO();
        action.setGameId(gameId);
        action.setPlayerId(playerId);
        action.setType(GameActionType.UNDO_LAST_MOVE);

        // Act
        gameController.undoLastMove(action);

        // Assert
        // Überprüfe, ob die Undo-Logik im GameManager aufgerufen wurde
        verify(gameManager, times(1)).undoLastMove(activePlayer);
        // Überprüfe, ob ein Update an alle Clients im Raum gesendet wurde
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/game/update/" + gameId), any(Room.class));
    }
    @Test
    void undoLastMove_WhenPlayerIsNotActive_ShouldNotUndoButBroadcast() {
        // Arrange
        PlayerActionDTO action = new PlayerActionDTO();
        action.setGameId(gameId);
        action.setPlayerId("someOtherPlayerId"); // Ein anderer Spieler
        action.setType(GameActionType.UNDO_LAST_MOVE);

        // Act
        gameController.undoLastMove(action);

        // Assert
        // Undo-Logik darf NICHT aufgerufen werden
        verify(gameManager, never()).undoLastMove(any(Player.class));
        // Ein Update wird trotzdem gesendet, um den Zustand zu synchronisieren (gutes Verhalten)
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/game/update/" + gameId), any(Room.class));
    }


    @Test
    void undoLastMove_WhenActivePlayerIsNull_ShouldNotUndo() {
        // Arrange
        when(gameManager.getActivePlayer()).thenReturn(null); // Kein aktiver Spieler
        PlayerActionDTO action = new PlayerActionDTO();
        action.setGameId(gameId);
        action.setPlayerId(playerId);

        // Act
        gameController.undoLastMove(action);

        // Assert
        verify(gameManager, never()).undoLastMove(any());
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/game/update/" + gameId), any(Room.class));
    }
}