package at.aau.serg.kingdombuilderserver.messaging.dtos;

import at.aau.serg.kingdombuilderserver.game.Player;
import at.aau.serg.kingdombuilderserver.game.Room;
import at.aau.serg.kingdombuilderserver.game.RoomStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomLobbyDTO {
    private String id;
    private String name;
    private int size;
    private int currentUsers;
    private RoomStatus status;
    private List<Player> players;

    public RoomLobbyDTO(String id, String name, int size, int currentUsers, RoomStatus status, List<Player> players) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.currentUsers = currentUsers;
        this.status = status;
        this.players = players;
    }

    // Hilfsmethode zum einfachen Erstellen aus einem Room-Objekt
    public static RoomLobbyDTO from(Room room) {
        // Spieler zu PlayerDTOs mappen
        List<Player> playerDTOs = room.getPlayers();
        return new RoomLobbyDTO(
                room.getId(),
                room.getName(),
                room.getSize(),
                room.getCurrentUsers(),
                room.getStatus(),
                playerDTOs
        );
    }
}
