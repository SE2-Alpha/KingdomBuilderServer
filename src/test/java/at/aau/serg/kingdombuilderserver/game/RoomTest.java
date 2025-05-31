package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String roomId = "room123";
        String roomName = "Test Room";
        int maxPlayers = 4;

        // Act
        Room room = new Room(roomId, roomName);

        // Assert
        assertEquals(roomId, room.getId());
        assertEquals(roomName, room.getName());
        assertEquals(maxPlayers, room.getSize());
        assertTrue(room.getPlayers().isEmpty());
    }

    @Test
    void testAddPlayer() {
        // Arrange
        Room room = new Room("room123", "Test Room");
        Player player = new Player("player1", "Player One");

        // Act
        room.addPlayer(player);

        // Assert
        assertEquals(1, room.getPlayers().size());
        assertTrue(room.getPlayers().contains(player));
    }

    @Test
    void testRemovePlayer() {
        // Arrange
        Room room = new Room("room123", "Test Room");
        Player player = new Player("player1", "Player One");
        room.addPlayer(player);

        // Act
        room.removePlayer(player.getId());

        // Assert
        assertTrue(room.getPlayers().isEmpty());
    }

    @Test
    void testIsFull() {
        // Arrange
        Room room = new Room("room123", "Test Room");
        room.addPlayer(new Player("player1", "Player One"));
        room.addPlayer(new Player("player2", "Player Two"));
        room.addPlayer(new Player("player3", "Player Three"));
        room.addPlayer(new Player("player4", "Player Four"));

        // Act
        boolean isFull = room.getSize() == room.getPlayers().size();

        // Assert
        assertTrue(isFull);
    }
}