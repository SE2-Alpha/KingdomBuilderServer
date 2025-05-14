package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import org.junit.jupiter.api.Test;

public class GameTest {

    @Test
    public void testGameHousePosition() {
        GameHousePosition gameHousePosition = new GameHousePosition();
    }

    @Test
    public void testPlayerActionDTOGetter(){
        PlayerActionDTO playerActionDTO = new PlayerActionDTO();
        playerActionDTO.setPlayerId("playerId");
        playerActionDTO.setGameId("gameId");
        playerActionDTO.setPlayerActive(true);
        playerActionDTO.setType(GameActionType.SPECIAL_ACTION);
        playerActionDTO.setPosition(null);
    }

    @Test
    public void testPlayerActionDTOSetter(){
        PlayerActionDTO playerActionDTO = new PlayerActionDTO();
        playerActionDTO.setPlayerId("playerId");
        playerActionDTO.setGameId("gameId");
        playerActionDTO.setPlayerActive(true);
        playerActionDTO.setType(GameActionType.SPECIAL_ACTION);
        playerActionDTO.setPosition(null);
    }
}
