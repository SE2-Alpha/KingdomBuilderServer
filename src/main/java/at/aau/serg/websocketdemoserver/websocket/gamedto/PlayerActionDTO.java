package at.aau.serg.websocketdemoserver.websocket.gamedto;

import at.aau.serg.websocketdemoserver.websocket.state.GameActionType;
import at.aau.serg.websocketdemoserver.websocket.state.GameHousePosition;
import lombok.Getter;

public class PlayerActionDTO {
    @Getter
    private GameActionType type; // PLACE_HOUSE oder SPECIAL_ACTION
    private GameHousePosition position; // für PLACE_HOUSE
    @Getter
    private String specialActionId; // für SPECIAL_ACTION

}
