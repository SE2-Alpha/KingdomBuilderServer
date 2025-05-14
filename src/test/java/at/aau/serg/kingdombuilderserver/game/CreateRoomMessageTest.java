package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateRoomMessageTest {

    // Test für den Getter und Setter
    @Test
    void testGetterSetter() {
        CreateRoomMessage createRoomMessage = new CreateRoomMessage();

        // Setze den Wert für playerId
        createRoomMessage.setPlayerId("player123");

        // Überprüfe, ob der Setter den Wert korrekt gesetzt hat
        assertEquals("player123", createRoomMessage.getPlayerId(), "Player ID should be 'player123'.");
    }
}
