package at.aau.serg.kingdombuilderserver.game;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Server message on Room Start:<br>
 * - Room ID<br>
 * - Player List<br> <br>
 * Message has to be mapped accordingly on the Client-side when receiving
 */
@Getter
@Setter
public class StartRoomMessage {
    private String roomId;
    private List<Player> players;

    StartRoomMessage(String roomId) {
        this.roomId = roomId;
        players = RoomList.list.get(roomId).getPlayers();
    }
}
