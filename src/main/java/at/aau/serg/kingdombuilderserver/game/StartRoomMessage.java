package at.aau.serg.kingdombuilderserver.game;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Server message on Room Start:<br>
 * - Room ID<br>
 * - Player List<br> <br>
 * PlayerList is directly retrieved from static RoomList.
 * Message has to be mapped accordingly on the Client-side when receiving
 */
@Getter
public final class StartRoomMessage {
    private final String roomId;
    private final List<Player> players;

    StartRoomMessage(String roomId) {
        this.roomId = roomId;
        players = RoomList.list.get(roomId).getPlayers();
    }

    public String toString(){
        return "Room " + roomId + " started with " + players.size() + " players.";
    }
}
