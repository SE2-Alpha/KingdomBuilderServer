package at.aau.serg.kingdombuilderserver.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {
    private final String id;
    private final String name;
    private final int size = 4;
    private final List<Player> players = new CopyOnWriteArrayList<>();
    private RoomStatus status = RoomStatus.WAITING;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getCurrentUsers() {
        return players.size();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void addPlayer(Player player) {
        if (players.size() >= size) {
            System.out.println("Room is full. Player " + player.getId() + " not added.");
            return;
        }
        players.add(player);
    }

    public void removePlayer(String playerId) {
        players.removeIf(p -> p.getId().equals(playerId));
    }

    public boolean checkIfPlayerInRoom(String playerId) {
        return players.stream().anyMatch(p -> p.getId().equals(playerId));
    }

    /**
     * Set Player Colors on Room start
     */
    public void setPlayerColor(){
        for(Player p: players){
            p.setColor(PlayerColor.getColor(players.indexOf(p)));
        }
    }
}
