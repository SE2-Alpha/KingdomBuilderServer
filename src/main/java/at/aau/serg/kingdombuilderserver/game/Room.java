package at.aau.serg.kingdombuilderserver.game;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Room {
    private final String id;
    private final String name;
    private final int size = 4;
    private final List<Player> players = new CopyOnWriteArrayList<>();
    @Setter
    private RoomStatus status = RoomStatus.WAITING;
    private GameManager gameManager;

    private static final Logger logger = LoggerFactory.getLogger(Room.class);

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void startGame() {
        if (players.size() < 1) { //TODO increase to 2 after debugging
            logger.warn("Not enough players to start the game. Minimum 2 players required.");
            return;
        }
        setPlayerColor();
        this.status = RoomStatus.STARTED;
        this.gameManager = new GameManager();
        this.gameManager.setActivePlayer(players.get(0)); // Set the first player as the active player
        logger.info("Game started in room {}", id);
    }

    public int getCurrentUsers() {
        return players.size();
    }

    public void addPlayer(Player player) {
        if (players.size() >= size) {
            logger.info("Room is full. Player {} not added.", player.getId());
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
