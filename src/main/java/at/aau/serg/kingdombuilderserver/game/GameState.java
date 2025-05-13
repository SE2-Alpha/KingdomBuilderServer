package at.aau.serg.kingdombuilderserver.game;

import lombok.Getter;

import java.util.List;

@Getter
public class GameState {

    private final String gameId;
    private final GameService turnManager;
    private final List<Player> players;

    public GameState(String gameId, List<String> playerIds) {
        this.gameId = gameId;
        this.turnManager = new GameService(playerIds);
        this.players = playerIds.stream().map(Player::new).toList();
    }
}
