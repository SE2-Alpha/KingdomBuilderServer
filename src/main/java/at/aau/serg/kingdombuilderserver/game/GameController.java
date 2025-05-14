package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    public final Map<String, Room> rooms = RoomList.list;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/placeHouses")
    public void placeHouse(@Payload PlayerActionDTO action) {
        logger.info("Received placeHouse request: {}", action);
        String gameId = action.getGameId();
        if (rooms.containsKey(gameId)) {
        }

    }

    @MessageMapping("/game/endTurn")
    public void endTurn(@Payload PlayerActionDTO action) {
        logger.info("Received endTurn request: {}", action);
        String gameId = action.getGameId();
        if (rooms.containsKey(gameId)) {
        }
    }

    @MessageMapping("/game/drawCard")
    public void drawCard(@Payload PlayerActionDTO action) {
        logger.info("Received drawCard request: {}", action);
        logger.info("Rooms: "+rooms.keySet());
        String gameId = action.getGameId();
        logger.info("Game ID: "+gameId);
        logger.info("Pid: "+action.getPlayerId());
        logger.info("Rooms: "+rooms);
        if (rooms.containsKey(gameId)) {
            logger.info("Card drawn by player {} in game {}", action.getPlayerId(), action.getGameId());
            Random random = new Random();
            int terrainCardType = random.nextInt(5);
            broadcastTerrainCardType(action.getGameId(), terrainCardType);
        } else {
            logger.warn("Game not found for gameId: {}", action.getGameId());
        }
    }

    private void broadcastGameState(String gameId) {
        if (rooms.containsKey(gameId)) {
            logger.info("Broadcasting game state for game: {}", gameId);
            messagingTemplate.convertAndSend("/topic/game/"+gameId, gameId);
        } else {
            logger.warn("Game not found for broadcasting: {}", gameId);
        }
    }

    private void broadcastTerrainCardType(String gameId, int terrainCardType){
        logger.info("Broadcasting terrain type for game: {}", gameId + terrainCardType);
        messagingTemplate.convertAndSend("/topic/game/card/"+gameId, terrainCardType);
    }
}
