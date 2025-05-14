package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    void testGameHousePosition() {
        assertThrows(UnsupportedOperationException.class, GameHousePosition::new, "Default constructor should throw UnsupportedOperationException.");
    }

    private PlayerActionDTO playerActionDTO;

    @BeforeEach
    void setUp() {
        playerActionDTO = new PlayerActionDTO();
        playerActionDTO.setPlayerId("playerId");
        playerActionDTO.setGameId("gameId");
        playerActionDTO.setPlayerActive(true);
        playerActionDTO.setType(GameActionType.PLACE_HOUSE);
        playerActionDTO.setPosition(null);
    }

    // Test für die Getter
    @Test
    void testPlayerActionDTOGetter() {
        assertEquals("playerId", playerActionDTO.getPlayerId(), "Player ID should be 'playerId'.");
        assertEquals("gameId", playerActionDTO.getGameId(), "Game ID should be 'gameId'.");
        assertTrue(playerActionDTO.isPlayerActive(), "Player should be active.");
        assertEquals(GameActionType.PLACE_HOUSE, playerActionDTO.getType(), "Action type should be PLACE_HOUSE.");
        assertNull(playerActionDTO.getPosition(), "Position should be null.");
    }

    // Test für die Setter
    @Test
    void testPlayerActionDTOSetter() {
        playerActionDTO.setPlayerId("newPlayerId");
        playerActionDTO.setGameId("newGameId");
        playerActionDTO.setPlayerActive(false);
        playerActionDTO.setType(GameActionType.SPECIAL_ACTION);
        playerActionDTO.setPosition(null);

        assertEquals("newPlayerId", playerActionDTO.getPlayerId(), "Player ID should be 'newPlayerId'.");
        assertEquals("newGameId", playerActionDTO.getGameId(), "Game ID should be 'newGameId'.");
        assertFalse(playerActionDTO.isPlayerActive(), "Player should not be active.");
        assertEquals(GameActionType.SPECIAL_ACTION, playerActionDTO.getType(), "Action type should be REGULAR_ACTION.");
        assertNull(playerActionDTO.getPosition(), "Position should be 'null'.");
    }
}
