package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerScoreDTO;
import at.aau.serg.kingdombuilderserver.messaging.dtos.RoomLobbyDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    public final Map<String, Room> rooms = RoomList.list;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    private void broadcastGameUpdate(Room room){
        logger.info("Broadcasting GameUpdate for game: {}", room.getId());
        System.out.println("Broadcasting GameUpdate for game: " + room.getId());
        messagingTemplate.convertAndSend("/topic/game/update/"+room.getId(), room);
    }

    @MessageMapping("/game/placeHouses")
    public void placeHouse(@Payload PlayerActionDTO action) {
        logger.info("Received placeHouse request: {}", action.getGameId());
        String gameId = action.getGameId();
        if (rooms.containsKey(gameId)) {
            if(action.getType().equals(GameActionType.PLACE_HOUSE)) {
                logger.info("Placing house for player {} at position {}", action.getPlayerId(), action.getPosition());
                Room room = rooms.get(gameId);
                GameManager gameManager = room.getGameManager();
                Player activePlayer = gameManager.getActivePlayer();

                if (activePlayer != null && activePlayer.getId().equals(action.getPlayerId())) {

                    gameManager.placeHouse(action.getPosition());
                    logger.info("House placed successfully for player {}", action.getPlayerId());
                } else {
                    logger.warn("Player {} is not the active player in game {}", action.getPlayerId(), gameId);
                }
            }
            broadcastGameUpdate(rooms.get(gameId));
        }

    }

    @MessageMapping("/game/endTurn")
    public void endTurn(@Payload PlayerActionDTO action) {
        logger.info("Received endTurn request: {}", action);
        String gameId = action.getGameId();
        if (rooms.containsKey(gameId)) {
            logger.info("Ending turn for player {} in game {}", action.getPlayerId(), gameId);
            Room room = rooms.get(gameId);
            GameManager gameManager = room.getGameManager();
            Player activePlayer = gameManager.getActivePlayer();

            if (activePlayer != null && activePlayer.getId().equals(action.getPlayerId())) {
                // Logik zum Beenden des Zuges, z.B. Wechsel zum nächsten Spieler
                gameManager.setActivePlayer(room.getNextPlayer(activePlayer));
                gameManager.nextRound();
                logger.info("Turn ended successfully for player {}", action.getPlayerId());
                broadcastGameUpdate(room);
            } else {
                logger.warn("Player {} is not the active player in game {}", action.getPlayerId(), gameId);
            }
        } else {
            logger.warn("Game not found for gameId: {}", action.getGameId());
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
            broadcastGameUpdate(rooms.get(gameId));
        } else {
            logger.warn("Game not found for gameId: {}", action.getGameId());
        }
    }
    @MessageMapping("/game/get")
    public void getGameUpdate(String gameId) {
        logger.info("Received getGameUpdate request for gameId: {}", gameId);
        if (rooms.containsKey(gameId)) {
            logger.info("Broadcasting game state for gameId: {}", gameId);
            broadcastGameUpdate(rooms.get(gameId));
        } else {
            logger.warn("Game not found for gameId: {}", gameId);
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

    @MessageMapping("/game/getScores")
    @SendTo("/topic/game/scores")
    public List<PlayerScoreDTO> sendPlayerScores(@Payload String gameId) {
        if (rooms.containsKey(gameId)) {
            Room room = rooms.get(gameId);
            GameManager gameManager = room.getGameManager();
            List<Player> players = room.getPlayers();

            WinningConditionEvaluator evaluator = new WinningConditionEvaluator(
                    gameManager.getGameBoard(), players
            );

            Map<String, Integer> scores = evaluator.getPlayerPoints();

            return scores.entrySet().stream()
                    .map(entry -> new PlayerScoreDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        } else {
            logger.warn("Game not found for gameId: {}", gameId);
            return Collections.emptyList();
        }
    }

}
