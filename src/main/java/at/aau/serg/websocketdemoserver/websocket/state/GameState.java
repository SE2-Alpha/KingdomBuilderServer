package at.aau.serg.websocketdemoserver.websocket.state;

import at.aau.serg.websocketdemoserver.websocket.controller.GamePlayer;
import at.aau.serg.websocketdemoserver.websocket.controller.GameTurnManager;
import lombok.Getter;

import java.util.List;

@Getter
public class GameState {

    private final String gameId;
    private final GameTurnManager turnManager;
    private final List<GamePlayer> players;

    public GameState(String gameId, List<String> playerIds) {
        this.gameId = gameId;
        this.turnManager = new GameTurnManager(playerIds);
        this.players = playerIds.stream().map(GamePlayer::new).toList();
    }
}
