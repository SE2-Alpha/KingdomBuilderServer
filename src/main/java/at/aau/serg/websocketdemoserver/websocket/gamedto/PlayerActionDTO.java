package at.aau.serg.websocketdemoserver.websocket.gamedto;

import at.aau.serg.websocketdemoserver.websocket.state.GameActionType;
import at.aau.serg.websocketdemoserver.websocket.state.GameHousePosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerActionDTO {
    private String gameId;
    private String playerId;
    private GameActionType type; // PLACE_HOUSE oder SPECIAL_ACTION
    private GameHousePosition position; // für PLACE_HOUSE
    private String specialActionId; // für SPECIAL_ACTION
}
