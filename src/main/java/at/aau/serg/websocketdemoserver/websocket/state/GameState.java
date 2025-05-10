package at.aau.serg.websocketdemoserver.websocket.state;

import at.aau.serg.websocketdemoserver.websocket.controller.GamePlayer;
import at.aau.serg.websocketdemoserver.websocket.controller.GameService;
import lombok.Getter;

import java.util.List;

@Getter
public class GameState {

    private final String gameId;
    private final GameService turnManager;
    private final List<GamePlayer> players;

    public GameState(String gameId, List<String> playerIds) {
        this.gameId = gameId;
        this.turnManager = new GameService(playerIds);
        this.players = playerIds.stream().map(GamePlayer::new).toList();
    }
}
