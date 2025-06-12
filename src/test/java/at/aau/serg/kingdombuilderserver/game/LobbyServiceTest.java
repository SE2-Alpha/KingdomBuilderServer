package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class LobbyServiceTest {

    @InjectMocks
    private LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        lobbyService = new LobbyService();
        RoomList.list.clear(); // Clear the room list before each test
    }

    @Test
    void testCreateRoom() {
        // Arrange
        String playerId = "player1";
        String playername = "player";

        // Act
        Room room = lobbyService.createRoom(playerId,playername);

        // Assert
        assertThat(room).isNotNull();
        assertThat(room.getPlayers()).hasSize(1);
        assertThat(room.getPlayers().get(0).getId()).isEqualTo(playerId);
    }

    @Test
    void testCreateRoomAlreadyIn() {
        // Arrange
        String playerId = "player1";
        String playername = "player";

        // Act
        Room room = lobbyService.createRoom(playerId,playername);
        Room room2 = lobbyService.createRoom(playerId,playername);

        // Assert
        assertThat(room).isNotNull();
        assertThat(room2).isNull();
        assertThat(room.getPlayers()).hasSize(1);
        assertThat(room.getPlayers().get(0).getId()).isEqualTo(playerId);
    }

    @Test
    void testJoinRoom() {
        // Arrange
        String playerId1 = "player1";
        String playername1 = "player1";
        String playerId2 = "player2";
        String playername2 = "player2";
        Room room = lobbyService.createRoom(playerId1,playername1);

        // Act
        Room joinedRoom = lobbyService.joinRoom(room.getId(), playerId2,playername2);
        Room joinedRoom2 = lobbyService.joinRoom(room.getId(), playerId2,playername2);

        // Assert
        assertThat(joinedRoom).isNotNull();
        assertThat(joinedRoom2).isNull();
        assertThat(joinedRoom.getPlayers()).hasSize(2);
        assertThat(joinedRoom.checkIfPlayerInRoom(playerId2)).isTrue();
    }

    @Test
    void testJoinRoomFull() {
        // Arrange
        String playerId1 = "player1";
        String playerId2 = "player2";
        String playerId3 = "player3";
        String playerId4 = "player4";
        String playerId5 = "player5";
        Room room = lobbyService.createRoom(playerId1,playerId1);

        // Act
        Room joinedRoom = lobbyService.joinRoom(room.getId(), playerId2,playerId1);
        Room joinedRoom2 = lobbyService.joinRoom(room.getId(), playerId3,playerId3);
        Room joinedRoom3 = lobbyService.joinRoom(room.getId(), playerId4,playerId4);
        Room joinedRoom4 = lobbyService.joinRoom(room.getId(), playerId5,playerId5);

        // Assert
        assertThat(joinedRoom).isNotNull();
        assertThat(joinedRoom2).isNotNull();
        assertThat(joinedRoom3).isNotNull();
        assertThat(joinedRoom4).isNotNull();
        assertThat(joinedRoom.getPlayers()).hasSize(4);
        assertThat(joinedRoom.checkIfPlayerInRoom(playerId2)).isTrue();
        assertThat(joinedRoom.checkIfPlayerInRoom(playerId5)).isFalse();
    }

    @Test
    void testLeaveRoom() {
        // Arrange
        String playerId = "player1";
        Room room = lobbyService.createRoom(playerId,playerId);

        // Act
        lobbyService.leaveRoom(room.getId(), playerId);
        Collection<Room> allRooms = lobbyService.getAllRooms();

        // Assert
        assertThat(allRooms).isEmpty();
    }

    @Test
    void testStartGame() {
        // Arrange
        String playerId1 = "player1";
        String playerId2 = "player2";
        Room room = lobbyService.createRoom(playerId1,playerId1);
        lobbyService.joinRoom(room.getId(), playerId2,playerId2);

        // Act
        lobbyService.startGame(room.getId());

        // Assert
        assertThat(room.getStatus()).isEqualTo(RoomStatus.STARTED);
    }

    @Test
    void testGetPlayerRoom() {
        // Arrange
        String playerId = "player1";
        Room room = lobbyService.createRoom(playerId,playerId);

        // Act
        String roomId = lobbyService.getPlayerRoom(playerId);

        // Assert
        assertThat(roomId).isEqualTo(room.getId());
    }

    @Test
    void testFinishGame() {
        // Arrange
        String playerId1 = "player1";
        String playerId2 = "player2";
        Room room = lobbyService.createRoom(playerId1,playerId1);
        lobbyService.joinRoom(room.getId(), playerId2,playerId2);

        // Act
        lobbyService.finishGame(room.getId());

        // Assert
        assertThat(room.getStatus()).isEqualTo(RoomStatus.FINISHED);
    }
}