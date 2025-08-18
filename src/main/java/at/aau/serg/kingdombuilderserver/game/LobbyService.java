package at.aau.serg.kingdombuilderserver.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Service
public class LobbyService {

    private static final Logger logger = LoggerFactory.getLogger(LobbyService.class);

    private final Map<String, Room> rooms = RoomList.list;

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public Room createRoom(String playerId, String userName) {
        if(getPlayerRoom(playerId) != null) {
            logger.info("Room with id {} already exists", playerId);
            return null;
        }
        String roomId = UUID.randomUUID().toString();
        String name = "Room " + (rooms.size() + 1); // z.B. "Room A", "Room B", etc.
        Room room = new Room(roomId, name);
        room.addPlayer(new Player(playerId, userName));
        rooms.put(roomId, room);
        logger.info("Rooms: {}", rooms);
        return room;
    }


    public Room joinRoom(String roomId, String playerId, String userName) {
        if(getPlayerRoom(playerId) != null) {
            logger.info("Player already in a room");
            return null;
        }
        logger.info("Joining room {} to player {}", roomId, playerId);
        Room room = rooms.get(roomId);
        if (room != null && room.getPlayers().size() < 4 && room.getStatus() == RoomStatus.WAITING && !room.checkIfPlayerInRoom(playerId)) {
            room.addPlayer(new Player(playerId,userName));
            logger.info("Player {} joined the room {}", playerId, roomId);
        } else {
            logger.info("Room is full or already started or player already in room");
        }

        return room;
    }

    public void leaveRoom(String roomId, String playerId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.removePlayer(playerId);
            if (room.getPlayers().isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }
    public void startGame(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null && room.getPlayers().size() > 1 && room.getStatus()!=RoomStatus.STARTED) {
            room.setPlayerColor();
            room.setStatus(RoomStatus.STARTED);
            room.startGame();
        }
    }

    public void finishGame(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.setStatus(RoomStatus.FINISHED);
        }
    }

    public String getPlayerRoom(String playerId) {
        for (Room room : rooms.values()) {
            if (room.checkIfPlayerInRoom(playerId)) {
                return room.getId();
            }
        }
        return null;
    }
}
