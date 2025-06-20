package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.CheatReportDTO;
import at.aau.serg.kingdombuilderserver.board.TerrainType;
import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import at.aau.serg.kingdombuilderserver.messaging.dtos.RoomLobbyDTO;
import io.micrometer.observation.GlobalObservationConvention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.*;

@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    public final Map<String, Room> rooms = RoomList.list;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private final Random random = new Random();


    private void broadcastGameUpdate(Room room){
        logger.info("Broadcasting GameUpdate for game: {}", room.getId());
        System.out.println("Broadcasting GameUpdate for game: " + room.getId());
        messagingTemplate.convertAndSend("/topic/game/update/"+room.getId(), room);
    }

    private void broadcastCheatReportWindow(Room room, boolean isActive, String reportedPlayerId){
        logger.info("Broadcasting CheatReportWindow status ({}) for game: {}",isActive, room.getId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("gameId", room.getId());
        payload.put("isWindowActive", isActive);
        payload.put("reportedPlayerId", reportedPlayerId);

        messagingTemplate.convertAndSend("/topic/game/cheatWindow/"+ room.getId(), payload);
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

    @MessageMapping("/game/undoLastMove")
    public void undoLastMove(@Payload PlayerActionDTO action){
        logger.info("Received undoLastMove request for game: {}", action.getGameId());
        String gameId = action.getGameId();
        if(rooms.containsKey(gameId)){
            Room room = rooms.get(gameId);
            GameManager gameManager = room.getGameManager();
            Player activePlayer = gameManager.getActivePlayer();

            if (activePlayer != null && activePlayer.getId().equals(action.getPlayerId())) {
                logger.info("Undoing last move for player {}", action.getPlayerId());
                gameManager.undoLastMove(activePlayer);
                logger.info("Last move undone successfully for player {}", action.getPlayerId());
            } else {
                logger.warn("Player {} is not the active player in game {}", action.getPlayerId(), gameId);
            }
            broadcastGameUpdate(room);
        } else {
            logger.warn("Game not found for gameId: {}", gameId);
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
            List<Integer> activeBuildings = gameManager.getActiveBuildingsSequence();

            if (activePlayer != null && activePlayer.getId().equals(action.getPlayerId())) {
                activePlayer.setCurrentCard(null);
                activeBuildings.clear();
                // Logik zum Beenden des Zuges, z.B. Wechsel zum nächsten Spieler
                logger.info("Received endTurn from Player {}. Client-Payload says didCheat={}", action.getPlayerId(), action.isDidCheat());
                activePlayer.setHasCheated(action.isDidCheat());
                logger.info("Player {}'s internal hasCheated flag is now set to: {}", activePlayer.getId(), activePlayer.hasCheated());

                logger.info("Initiating cheat report window for game {}", gameId);

                // Cheat-Report-Fenster aktivieren"
                gameManager.setAwaitingCheatReports(true);
                broadcastCheatReportWindow(room, true, activePlayer.getId());

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
                        if (nextPlayer != null && nextPlayer.isSkippedTurn()) {
                            logger.info("Player " + nextPlayer.getId() + " is skipping their turn.");
                            nextPlayer.setSkippedTurn(false); // WICHTIG: Flag für die Zukunft zurücksetzen
                            nextPlayer = room.getNextPlayer(nextPlayer); // Erneut aufrufen, um den Übernächsten Spieler zu bekommen
                        }

                        gameManager.setActivePlayer(nextPlayer);
                        if (nextPlayer != null) {
                            gameManager.nextRound();
                        } else {
                            logger.info("No next player found, potentially game end for game {}", gameId);
                        }

                        // Game update senden
                        broadcastGameUpdate(room);
                    }
                }, 5000); // 5 Sekunden Delay (Zeit für das Entlarfen)

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
        Room room = rooms.get(gameId);
        GameManager gameManager = room.getGameManager();
        Player activePlayer = gameManager.getActivePlayer();
        if (rooms.containsKey(gameId)) {
            if (activePlayer != null && activePlayer.getId().equals(action.getPlayerId())) {
                logger.info("Card drawn by player {} in game {}", action.getPlayerId(), action.getGameId());
                TerrainType terrainCardType = TerrainType.fromInt(random.nextInt(5)); //TODO(): Send ENUM instead of int
                room.getGameManager().getActivePlayer().setCurrentCard(terrainCardType);
                broadcastTerrainCardType(action.getGameId(), terrainCardType.toInt());
                broadcastGameUpdate(rooms.get(gameId));
            }else{
                logger.warn("Player {} is not the active player in game {}", action.getPlayerId(), gameId);
            }

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
    @MessageMapping("/game/reportCheat")
    public void reportCheat(@Payload CheatReportDTO report) {
        logger.info("Received cheat report: {}", report);
        String gameId = report.getGameId();

        if (rooms.containsKey(gameId)) {
            Room room = rooms.get(gameId);
            GameManager gameManager = room.getGameManager();
            if (gameManager == null) {
                logger.error("GameManager not found for room: {}", gameId);
                return;
            }

            // Prüfen, ob gerade Meldungen erwartet werden
            if (gameManager.isAwaitingCheatReports()){
                gameManager.recordCheatReport(report.getReporterPlayerId(),report.getReportedPlayerId());
                Player reporter = room.getPlayerById(report.getReporterPlayerId());
                Player reported = room.getPlayerById(report.getReportedPlayerId());

                if (reporter != null && reported != null) {
                // Sicherstellen, dass der gemeldete Spieler der aktive Spier ist (optional, aber sinnvoll)
                if (gameManager.getActivePlayer() != null && gameManager.getActivePlayer().getId().equals(reported.getId())) {
                    // Die eigentliche Aufzeichnung des Reports geschieht im Gamemanager
                    logger.info("Player {} reported player {} for cheating in game {}", reporter.getId(), reported.getId(), gameId);
                } else {
                    logger.warn("Cheat report invalid: Reported player {} is not the current active player in game {}.", reported.getId(), gameId);
                }
            } else {
                logger.warn("Reporter {} or reported player {} not found in game {}.", report.getReporterPlayerId(), report.getReportedPlayerId(), gameId);
            }
        } else {
            logger.warn("Cheat report received for game {} but not currently awaiting reports", gameId);
        }
        // Kein broadcastGameUpdate hier, da dies die 3-Sekunden-Phase stören könnte.
        // Die Auswertung erfolgt gesammelt in processCheatReportOutcome.
    } else {
        logger.warn("Game not found for gameId in cheat report: {}", report.getGameId());
        }
    }
}
