package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private Map<String, GameState> games = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/placeHouses")
    public void placeHouse(@Payload PlayerActionDTO action) {
        logger.info("Received placeHouse request: {}", action);
        GameState game = games.get(action.getGameId());

        if (game != null) {
            boolean success = game.getTurnManager().performAction(action.getPlayerId(), action);
            if (success) {
                logger.info("House placed by player {} in game {}", action.getPlayerId(), action.getGameId());
                broadcastGameState(action.getGameId());
            } else {
                logger.warn("Failed to place house for player {} in game {}", action.getPlayerId(), action.getGameId());
            }
        } else {
            logger.warn("Game not found for gameId: {}", action.getGameId());
        }
    }

    @MessageMapping("/game/endTurn")
    public void endTurn(@Payload PlayerActionDTO action) {
        logger.info("Received endTurn request: {}", action);
        GameState game = games.get(action.getGameId());

        if (game != null) {
            boolean success = game.getTurnManager().endTurn(action.getPlayerId());
            if (success) {
                logger.info("Turn ended by player {} in game {}", action.getPlayerId(), action.getGameId());
                broadcastGameState(action.getGameId());
            } else {
                logger.warn("Failed to end turn for player {} in game {}", action.getPlayerId(), action.getGameId());
            }
        } else {
            logger.warn("Game not found for gameId: {}", action.getGameId());
        }
    }

    @MessageMapping("/game/drawCard")
    public void drawCard(@Payload PlayerActionDTO action) {
        logger.info("Received drawCard request: {}", action);
        GameState game = games.get(action.getGameId());

        if (game != null) {
            boolean success = game.getTurnManager().drawCard(action.getPlayerId());
            if (success) {
                logger.info("Card drawn by player {} in game {}", action.getPlayerId(), action.getGameId());
                broadcastGameState(action.getGameId());
            } else {
                logger.warn("Failed to draw card for player {} in game {}", action.getPlayerId(), action.getGameId());
            }
        } else {
            logger.warn("Game not found for gameId: {}", action.getGameId());
        }
    }

    private void broadcastGameState(String gameId) {
        GameState game = games.get(gameId);
        if (game != null) {
            logger.info("Broadcasting game state for game: {}", gameId);
            messagingTemplate.convertAndSend("/topic/game/" + gameId, game);
        } else {
            logger.warn("Game not found for broadcasting: {}", gameId);
        }
    }

}
