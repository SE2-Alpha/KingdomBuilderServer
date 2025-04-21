package at.aau.serg.websocketdemoserver.websocket.state;

import at.aau.serg.websocketdemoserver.websocket.controller.GamePlayer;
import at.aau.serg.websocketdemoserver.websocket.controller.GameTurnManager;
import lombok.Getter;

import java.util.List;

public class GameState {

    @Getter
    private String gameId;
    @Getter
    private GameTurnManager turnManager;
    @Getter
    private List<GamePlayer> players;

    public GameState(String gameId, List<String> playerIds) {
        this.gameId = gameId;
        this.turnManager = new GameTurnManager(playerIds);
        this.players = playerIds.stream().map(GamePlayer::new).toList();
    }
}
