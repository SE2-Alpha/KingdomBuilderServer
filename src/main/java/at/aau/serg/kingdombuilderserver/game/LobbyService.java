package at.aau.serg.kingdombuilderserver.game;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LobbyService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public Room createRoom(String playerId) {
        if(getPlayerRoom(playerId) != null) {
            System.out.println("Player already in a room");
            return null;
        }
        String roomId = UUID.randomUUID().toString();
        String name = "Room " + (rooms.size() + 1); // z.B. "Room A", "Room B", etc.
        Room room = new Room(roomId, name);
        //room.addPlayer(new Player(playerId));
        rooms.put(roomId, room);
        return room;
    }


    public Room joinRoom(String roomId, String playerId) {
        if(getPlayerRoom(playerId) != null) {
            System.out.println("Player already in a room");
            return null;
        }
        System.out.println("Joining room " + roomId + " with player " + playerId);
        Room room = rooms.get(roomId);
        if (room != null && room.getPlayers().size() < 4 && room.getStatus() == RoomStatus.WAITING && !room.checkIfPlayerInRoom(playerId)) {
            room.addPlayer(new Player(playerId));
            System.out.println("Joining room " + roomId + " " + room.getCurrentUsers());
        } else {
            System.out.println("Room is full or already started or player already in room");
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
        if (room != null && room.getPlayers().size() >= 2) {
            room.setStatus(RoomStatus.STARTED);
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
