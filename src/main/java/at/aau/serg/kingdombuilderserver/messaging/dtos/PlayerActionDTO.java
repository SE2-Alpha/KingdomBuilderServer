package at.aau.serg.kingdombuilderserver.messaging.dtos;

import at.aau.serg.kingdombuilderserver.game.GameActionType;
import at.aau.serg.kingdombuilderserver.game.GameHousePosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerActionDTO {
    private String gameId;
    private String playerId;
    private boolean isPlayerActive;
    private GameActionType type; // PLACE_HOUSE oder SPECIAL_ACTION
    private GameHousePosition position; // für PLACE_HOUSE
    private String specialActionId; // für SPECIAL_ACTION

}
