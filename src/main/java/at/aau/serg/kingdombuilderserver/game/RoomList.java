package at.aau.serg.kingdombuilderserver.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomList {
    public static RoomList rooms;
    public static final Map<String, Room> list = new ConcurrentHashMap<>();

    private RoomList() {
    }

    public static RoomList getInstance() {
        return rooms;
    }
}
