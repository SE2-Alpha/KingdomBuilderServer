package at.aau.serg.kingdombuilderserver.controller;

import at.aau.serg.kingdombuilderserver.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LobbyControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private LobbyService lobbyService;

    @InjectMocks
    private LobbyController lobbyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRooms() {
        // Arrange
        Room room = new Room("1", "Test Room");
        when(lobbyService.getAllRooms()).thenReturn(Collections.singletonList(room));

        // Act
        Collection<Room> response = lobbyController.getLobby();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.iterator().next().getName()).isEqualTo("Test Room");
        verify(lobbyService, times(2)).getAllRooms(); // Anzahl der tatsächlichen Aufrufe anpassen
    }

    @Test
    void testCreateRoom() {
        // Arrange
        String playerId = "player1";
        CreateRoomMessage createRoomMessage = new CreateRoomMessage();
        createRoomMessage.setPlayerId(playerId);
        Room room = new Room("1", "Test Room");
        when(lobbyService.createRoom(playerId)).thenReturn(room);

        // Act
        lobbyController.createRoom(createRoomMessage);

        // Assert
        verify(lobbyService, times(1)).createRoom(playerId);
    }

    @Test
    void testJoinRoom() {
        // Arrange
        String roomId = "1";
        String playerId = "player1";
        Room room = new Room(roomId, "Test Room");
        JoinRoomMessage joinRoomMessage = new JoinRoomMessage();
        joinRoomMessage.setRoomId(roomId);
        joinRoomMessage.setPlayerId(playerId);

        when(lobbyService.joinRoom(roomId, playerId)).thenReturn(room);

        // Act
        lobbyController.joinRoom(joinRoomMessage);

        // Assert
        verify(lobbyService, times(1)).joinRoom(roomId, playerId);
    }

    @Test
    void testLeaveRoom() {
        // Arrange
        String roomId = "1";
        String playerId = "player1";

        LeaveRoomMessage leaveRoomMessage = new LeaveRoomMessage();
        leaveRoomMessage.setRoomId(roomId);
        leaveRoomMessage.setPlayerId(playerId);

        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/lobby"), any(Object.class));

        // Act
        lobbyController.leaveRoom(leaveRoomMessage);

        // Assert
        verify(lobbyService, times(1)).leaveRoom(roomId, playerId);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/lobby"), any(Object.class));
    }

    @Test
    void testStartGame() {
        // Arrange
        String roomId = "1";
        LeaveRoomMessage leaveRoomMessage = new LeaveRoomMessage();
        leaveRoomMessage.setRoomId(roomId);

        // Act
        lobbyController.startRoom(leaveRoomMessage);

        // Assert
        //assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(lobbyService, times(1)).startGame(roomId);
    }
}