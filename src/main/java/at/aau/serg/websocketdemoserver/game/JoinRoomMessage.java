package at.aau.serg.websocketdemoserver.game;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinRoomMessage {
    private String roomId;
    private String playerId;

}
