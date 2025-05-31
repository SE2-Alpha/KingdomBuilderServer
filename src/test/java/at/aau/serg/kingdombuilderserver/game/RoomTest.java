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
    void testAddPlayerTooMuch() {
        // Arrange
        Room room = new Room("room123", "Test Room");
        Player player = new Player("player1", "Player One");
        Player player2 = new Player("player2", "Player Two");
        Player player3 = new Player("player3", "Player Three");
        Player player4 = new Player("player4", "Player Four");
        Player player5 = new Player("player5", "Player Five");

        // Act
        room.addPlayer(player);
        room.addPlayer(player2);
        room.addPlayer(player3);
        room.addPlayer(player4);
        room.addPlayer(player5); // This should not be added as it exceeds the max players


        // Assert
        assertEquals(4, room.getCurrentUsers());
        assertFalse(room.getPlayers().contains(player5));
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
        boolean isFull = room.getSize() == room.getCurrentUsers();

        // Assert
        assertTrue(isFull);
    }

    @Test
    void testStartGame() {
        // Arrange
        Room room = new Room("room123", "Test Room");
        room.addPlayer(new Player("player1", "Player One"));
        room.addPlayer(new Player("player2", "Player Two"));
        room.addPlayer(new Player("player3", "Player Three"));
        room.addPlayer(new Player("player4", "Player Four"));

        // Act
        room.startGame();


        // Assert
        assertTrue(room.getStatus() == RoomStatus.STARTED, "Room should be in STARTED status after starting the game.");
    }

    @Test
    void testStartGameEmpty() {
        // Arrange
        Room room = new Room("room123", "Test Room");

        // Act
        room.startGame();


        // Assert
        assertFalse(room.getStatus() == RoomStatus.STARTED, "Room should not be in STARTED status if there are no players.");
    }

    @Test
    void testGetNextPlayer() {
        // Arrange
        Room room = new Room("room123", "Test Room");
        Player player = new Player("player1", "Player One");
        Player player2 = new Player("player2", "Player Two");
        Player player3 = new Player("player3", "Player Three");
        Player player4 = new Player("player4", "Player Four");
        Player player5 = new Player("player5", "Player Five");

        // Act
        room.addPlayer(player);
        room.addPlayer(player2);
        room.addPlayer(player3);
        room.addPlayer(player4);
        room.addPlayer(player5); // This should not be added as it exceeds the max players

        Player next = room.getNextPlayer(player2);


        // Assert
        assertEquals(player3, next);
    }
    @Test
    void testGetNextPlayerFalse() {
        // Arrange
        Room room = new Room("room123", "Test Room");
        Player player = new Player("player1", "Player One");
        Player player2 = new Player("player2", "Player Two");
        Player player3 = new Player("player3", "Player Three");
        Player player4 = new Player("player4", "Player Four");
        Player player5 = new Player("player5", "Player Five");

        // Act
        room.addPlayer(player);
        room.addPlayer(player2);
        room.addPlayer(player3);
        room.addPlayer(player4);
        room.addPlayer(player5); // This should not be added as it exceeds the max players

        assertThrows(IllegalArgumentException.class, () -> {
            Player next = room.getNextPlayer(player5);
        });

    }

}