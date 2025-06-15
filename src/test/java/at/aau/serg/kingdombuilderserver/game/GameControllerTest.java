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
        gameController.rooms.clear();
        gameController.rooms.put(gameId, room);

        when(room.getId()).thenReturn(gameId);
        when(gameManager.getActivePlayer()).thenReturn(activePlayer);
        when(activePlayer.getId()).thenReturn(playerId);
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
    void reportCheat_ReportedPlayerNotActive() {
        // Arrange
        CheatReportDTO report = new CheatReportDTO();
        report.setGameId(gameId);
        report.setReporterPlayerId("reporterPlayer");
        report.setReportedPlayerId("someOtherPlayer"); // NOT the active player

        Player reporterPlayer = mock(Player.class);
        Player reportedPlayer = mock(Player.class);
        when(reportedPlayer.getId()).thenReturn("someOtherPlayer");

        when(room.getPlayerById(report.getReporterPlayerId())).thenReturn(reporterPlayer);
        when(room.getPlayerById(report.getReportedPlayerId())).thenReturn(reportedPlayer);
        when(gameManager.isAwaitingCheatReports()).thenReturn(true);

        // Act
        gameController.reportCheat(report);

        // Assert
        verify(gameManager, never()).recordCheatReport(any(), any());
    }
}