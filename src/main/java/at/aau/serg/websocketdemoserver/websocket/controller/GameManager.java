package at.aau.serg.websocketdemoserver.websocket.controller;

import at.aau.serg.websocketdemoserver.websocket.gamedto.PlayerActionDTO;
import at.aau.serg.websocketdemoserver.websocket.state.GameState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private Map<String, GameState> games = new HashMap<>();

    public String createGame(List<String> playerIds) {
        String gameId = UUID.randomUUID().toString();
        GameState gameState = new GameState(gameId, playerIds);
        games.put(gameId, gameState);
        return gameId;
    }

    public GameState getGame(String gameId) {
        GameState game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Kein Spiel mit dieser ID gefunden.");
        }
        return game;
    }

    public void placeHouse(String gameId, String playerId, PlayerActionDTO action) {
        GameState game = getGame(gameId);
        game.getTurnManager().performAction(playerId, action);
    }

    public void endTurn(String gameId, String playerId) {
        GameState game = getGame(gameId);
        game.getTurnManager().endTurn(playerId);
    }

    public GameTurnManager getTurnManager(String gameId) {
        return getGame(gameId).getTurnManager();
    }
}
