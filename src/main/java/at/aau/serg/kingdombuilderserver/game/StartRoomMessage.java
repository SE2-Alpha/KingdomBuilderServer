package at.aau.serg.kingdombuilderserver.game;

import lombok.Getter;
import lombok.Setter;

/**
 * Server message on Room Start:<br>
 * - Room ID<br>
 * - Player List<br> <br>
 * Message has to be mapped accordingly on Client-side when receiving
 */
@Getter
@Setter
public class StartRoomMessage {
    private String roomId;
    private Player[] player;
}
