package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import at.aau.serg.kingdombuilderserver.messaging.dtos.RoomLobbyDTO;
import io.micrometer.observation.GlobalObservationConvention;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
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


    private void broadcastGameUpdate(Room room){
        logger.info("Broadcasting GameUpdate for game: {}", room.getId());
        System.out.println("Broadcasting GameUpdate for game: " + room.getId());
        messagingTemplate.convertAndSend("/topic/game/update/"+room.getId(), room);
    }

    private void broadcastCheatReportWindow(Room room){
        logger.info("Broadcasting CheatReportWindoow status for game: {}", room.getId());
        messagingTemplate.convertAndSend("/topic/game/cheatReportWindow/"+ room.getId(),
                Map.of("gameId", room.getId(), "isCheatReportWindowActive", true, "durationSeconds", 3));
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
                logger.info("Initiating cheat report window for game {}", gameId);

                // Cheat-Report-Fenster aktivieren"
                gameManager.setAwaitingCheatReports(true);
                broadcastCheatReportWindow(room);

                // Timer für 3 Sekunden aktivieren
                new java.util.Timer().schedule(new java.util.TimerTask(){
                    @Override
                    public void run(){
                        logger.info("Processing cheat reports for game {}", gameId);

                        // Cheat-Auswertung durchführen
                        gameManager.processCheatReportOutcome();

                        // Reset & cleanup
                        gameManager.setAwaitingCheatReports(false);
                        gameManager.cleanupTurn();

                        Player nextPlayer = room.getNextPlayer(activePlayer);
                        gameManager.setActivePlayer(nextPlayer);
                        if (nextPlayer != null) {
                            gameManager.nextRound();
                        } else {
                            logger.info("No next player found, potentially game end for game {}", gameId);
                        }

                        // Game update senden
                        broadcastGameUpdate(room);
                    }
                }, 3000); // 3 Sekunden Delay (Zeit für das Entlarfen)

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

    @MessageMapping("/game/cheat")
    public void handleCheat(@Payload PlayerActionDTO action) {
        logger.info("Received cheat attempt from player {} in game {}", action.getPlayerId(), action.getGameId());

        String gameID = action.getGameId();
        if (rooms.containsKey(gameID)){
            Room room = rooms.get(gameID);
            GameManager gameManager = room.getGameManager();
            Player activePlayer = gameManager.getActivePlayer();

            if (activePlayer != null && activePlayer.getId().equals(action.getPlayerId())){
               activePlayer.setHasCheated(true);

               // zusätzliches Haus setzen
                gameManager.placeHouse(action.getPosition());

                logger.info("Player {} placed an extra (cheated) house in game {}", action.getPlayerId(), gameID);

                       broadcastGameUpdate(room);
                } else {
                logger.warn("Player {} tried to cheat but is not the active player", action.getPlayerId());
            }
            } else {
            logger.warn("Game not found for gameId: {}", action.getGameId());
        }
    }
}
