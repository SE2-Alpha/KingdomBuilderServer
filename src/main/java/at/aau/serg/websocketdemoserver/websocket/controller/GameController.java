package at.aau.serg.websocketdemoserver.websocket.controller;

import at.aau.serg.websocketdemoserver.websocket.gamedto.PlayerActionDTO;
import at.aau.serg.websocketdemoserver.websocket.state.GameState;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class GameController {

    private Map<String, GameState> games = new HashMap<>();

    @MessageMapping("/game/create")
    @SendTo("/topic/game-updates")
    public GameState createGame(@Payload List<String> playerIds) {
        String gameId = UUID.randomUUID().toString();
        GameState gameState = new GameState(gameId, playerIds);
        games.put(gameId, gameState);
        return gameState;
    }

    @MessageMapping("/game/place-house")
    @SendTo("/topic/game-updates")
    public GameState placeHouse(@Payload PlayerActionDTO action) {
        GameState game = games.get(action.getGameId());
        if (game != null) {
            boolean success = game.getTurnManager().performAction(action.getPlayerId(), action);
            if (success) {
                return game;
            }
        }
        return null;
    }

    @MessageMapping("/game/end-turn")
    @SendTo("/topic/game-updates")
    public GameState endTurn(@Payload PlayerActionDTO action) {
        GameState game = games.get(action.getGameId());
        if (game != null) {
            boolean success = game.getTurnManager().performAction(action.getPlayerId(), action);
            if (success) {
                return game;
            }
        }
        return null;
    }

    @MessageMapping("/game/draw-card")
    @SendTo("/topic/game-updates")
    public GameState drawCard(@Payload PlayerActionDTO action) {
        GameState game = games.get(action.getGameId());
        if (game != null) {
            boolean success = game.getTurnManager().performAction(action.getPlayerId(), action);
            if (success) {
                return game;
            }
        }
        return null;
    }
}
