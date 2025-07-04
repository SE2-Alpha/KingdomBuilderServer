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
        if (players.size() < 2) {
            logger.warn("Not enough players to start the game. Minimum 2 players required.");
            return;
        }
        setPlayerColor();
        this.status = RoomStatus.STARTED;
        this.gameManager = new GameManager(this.players);
        this.gameManager.setActivePlayer(players.get(0)); // Set the first player as the active player
        logger.info("Active Player set to {}",players.get(0));
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

    public Player getNextPlayer(Player activePlayer) {
        int currentIndex = players.indexOf(activePlayer);
        if (currentIndex == -1) {
            throw new IllegalArgumentException("Active player not found in the room.");
        }
        int nextIndex = (currentIndex + 1) % players.size();
        return players.get(nextIndex);
    }

    public Player getPlayerById(String playerId) {
        if (playerId == null || playerId.isEmpty()){
            logger.warn("getPlayerById called with null or empty playerId.");
            return null;
        }
        for (Player player : this.players){
            if (player.getId().equals(playerId)){
                return player; // Spieler gefunden
            }
        }
        logger.debug("Player with id {} not found in room {}.", playerId, this.id);
        return null; // Spieler nicht in diesem Raum gefunden
    }
}
