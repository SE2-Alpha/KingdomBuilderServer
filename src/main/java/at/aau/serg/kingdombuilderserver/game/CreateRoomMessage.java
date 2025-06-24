package at.aau.serg.kingdombuilderserver.game;

import lombok.Getter;
import lombok.Setter;

public class CreateRoomMessage {
    // Getter und Setter
    @Getter
    @Setter
    private String playerId;
    @Getter
    @Setter
    private String userName;


}
