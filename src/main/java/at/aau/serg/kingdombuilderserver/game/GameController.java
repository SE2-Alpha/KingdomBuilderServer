package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GameController {

    private Map<String, GameState> games = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/placeHouses")
    public void placeHouse(@Payload PlayerActionDTO action) {
        System.out.println("Received placeHouse request: " + action);
        GameState game = games.get(action.getGameId());

        if (game != null) {
            boolean success = game.getTurnManager().performAction(action.getPlayerId(), action);
            if (success) {
                System.out.println("House placed by player " + action.getPlayerId() + " in game " + action.getGameId());
                broadcastGameState(action.getGameId());
            } else {
                System.out.println("Failed to place house for player " + action.getPlayerId() + " in game " + action.getGameId());
            }
        } else {
            System.out.println("Game not found for gameId: " + action.getGameId());
        }
    }

    @MessageMapping("/game/endTurn")
    public void endTurn(@Payload PlayerActionDTO action) {
        System.out.println("Received endTurn request: " + action);
        GameState game = games.get(action.getGameId());

        if (game != null) {
            boolean success = game.getTurnManager().endTurn(action.getPlayerId());
            if (success) {
                System.out.println("Turn ended by player " + action.getPlayerId() + " in game " + action.getGameId());
                broadcastGameState(action.getGameId());
            } else {
                System.out.println("Failed to end turn for player " + action.getPlayerId() + " in game " + action.getGameId());
            }
        } else {
            System.out.println("Game not found for gameId: " + action.getGameId());
        }
    }

    @MessageMapping("/game/drawCard")
    public void drawCard(@Payload PlayerActionDTO action) {
        System.out.println("Received drawCard request: " + action);
        GameState game = games.get(action.getGameId());

        if (game != null) {
            boolean success = game.getTurnManager().drawCard(action.getPlayerId());
            if (success) {
                System.out.println("Card drawn by player " + action.getPlayerId() + " in game " + action.getGameId());
                broadcastGameState(action.getGameId());
            } else {
                System.out.println("Failed to draw card for player " + action.getPlayerId() + " in game " + action.getGameId());
            }
        } else {
            System.out.println("Game not found for gameId: " + action.getGameId());
        }
    }

    private void broadcastGameState(String gameId) {
        GameState game = games.get(gameId);
        if (game != null) {
            System.out.println("Broadcasting game state for game: " + gameId);
            messagingTemplate.convertAndSend("/topic/game/" + gameId, game);
        } else {
            System.out.println("Game not found for broadcasting: " + gameId);
        }
    }

}
