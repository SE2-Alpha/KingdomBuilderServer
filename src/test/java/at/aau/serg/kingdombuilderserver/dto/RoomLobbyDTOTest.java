package at.aau.serg.kingdombuilderserver.dto;

import at.aau.serg.kingdombuilderserver.messaging.dtos.RoomLobbyDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomLobbyDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String roomId = "room123";
        String roomName = "Test Room";
        int maxPlayers = 4;

        // Act
        RoomLobbyDTO roomLobbyDTO = new RoomLobbyDTO(roomId, roomName, maxPlayers, 0, null, null);

        // Assert
        assertEquals(roomId, roomLobbyDTO.getId());
        assertEquals(roomName, roomLobbyDTO.getName());
        assertEquals(maxPlayers, roomLobbyDTO.getSize());
    }

    @Test
    void testSetters() {
        // Arrange
        RoomLobbyDTO roomLobbyDTO = new RoomLobbyDTO("", "", 0, 0, null, null);
        String roomId = "room456";
        String roomName = "New Room";
        int maxPlayers = 6;

        // Act
        roomLobbyDTO.setId(roomId);
        roomLobbyDTO.setName(roomName);
        roomLobbyDTO.setSize(maxPlayers);

        // Assert
        assertEquals(roomId, roomLobbyDTO.getId());
        assertEquals(roomName, roomLobbyDTO.getName());
        assertEquals(maxPlayers, roomLobbyDTO.getSize());
    }
}