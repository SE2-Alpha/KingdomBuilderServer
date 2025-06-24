package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.Test;

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
        assertSame(RoomStatus.STARTED, room.getStatus(),"Room should be in STARTED status after starting the game.");
    }

    @Test
    void testStartGameEmpty() {
        // Arrange
        Room room = new Room("room123", "Test Room");

        // Act
        room.startGame();


        // Assert
        assertNotSame(RoomStatus.STARTED, room.getStatus(), "Room should not be in STARTED status if there are no players.");
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
            room.getNextPlayer(player5);
        });

    }

    @Test
    void testGetPlayerById() {
        Room room = new Room("room123", "Test Room");
        Player player1 = new Player("p1", "Player One");
        room.addPlayer(player1);

        assertEquals(player1, room.getPlayerById("p1"));
        assertNull(room.getPlayerById("p2"), "Should return null for a non-existent player.");
        assertNull(room.getPlayerById(null), "Should return null for a null ID.");
        assertNull(room.getPlayerById(""), "Should return null for an empty ID.");
    }

    @Test
    void testGetNextPlayer_StandardAndWrapAround() {
        Room room = new Room("room123", "Test Room");
        Player player1 = new Player("p1", "Player One");
        Player player2 = new Player("p2", "Player Two");
        Player player3 = new Player("p3", "Player Three");
        room.addPlayer(player1);
        room.addPlayer(player2);
        room.addPlayer(player3);

        assertEquals(player2, room.getNextPlayer(player1), "Next player after p1 should be p2.");
        assertEquals(player3, room.getNextPlayer(player2), "Next player after p2 should be p3.");
        assertEquals(player1, room.getNextPlayer(player3), "Next player should wrap around from p3 to p1.");
    }

    @Test
    void testRemoveNonExistentPlayer() {
        Room room = new Room("room123", "Test Room");
        Player player1 = new Player("p1", "Player One");
        room.addPlayer(player1);

        // Try to remove a player who is not in the room
        room.removePlayer("p2");

        assertEquals(1, room.getCurrentUsers(), "Removing a non-existent player should not change the player count.");
    }
}