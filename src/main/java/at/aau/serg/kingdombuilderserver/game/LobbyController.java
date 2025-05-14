package at.aau.serg.kingdombuilderserver.game;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Comparator;

@Controller
public class LobbyController {

    private final LobbyService lobbyService;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyController(LobbyService lobbyService, SimpMessagingTemplate messagingTemplate) {
        this.lobbyService = lobbyService;
        this.messagingTemplate = messagingTemplate;
    }

    // Initialer Lobby‑State
    @MessageMapping("/lobby/get")
    @SendTo("/topic/lobby")
    public Collection<Room> getLobby() {
        System.out.println("Lobby get"+lobbyService.getAllRooms().size());
        return lobbyService.getAllRooms()
                .stream()
                .sorted(Comparator.comparing(Room::getName))
                .toList();
    }

    // Raum erstellen
    @MessageMapping("/lobby/create")
    public void createRoom(@Payload CreateRoomMessage msg) {
        lobbyService.createRoom(msg.getPlayerId());
        broadcastLobby();
    }

    // Raum beitreten
    @MessageMapping("/lobby/join")
    public void joinRoom(@Payload JoinRoomMessage msg) {
        lobbyService.joinRoom(msg.getRoomId(), msg.getPlayerId());
        broadcastLobby();
    }

    // Raum verlassen
    @MessageMapping("/lobby/leave")
    public void leaveRoom(@Payload LeaveRoomMessage msg) {
        lobbyService.leaveRoom(msg.getRoomId(), msg.getPlayerId());
        broadcastLobby();
    }

    // Raum Starten
    @MessageMapping("/lobby/start")
    public void startRoom(@Payload LeaveRoomMessage msg) {
        lobbyService.startGame(msg.getRoomId());
        broadcastRoom(new StartRoomMessage(msg.getRoomId()));
        System.out.println("TODO!! Start room " + msg.getRoomId());
    }

    // Hilfsmethode: Aktualisierten Lobby‑State an alle senden
    private void broadcastLobby() {
        System.out.println("Lobby broadcast "+lobbyService.getAllRooms().size());
        messagingTemplate.convertAndSend("/topic/lobby", lobbyService.getAllRooms());
    }

    //Sendet Player-Liste mit an alle Clients in einem Raum
    private void broadcastRoom(StartRoomMessage msg){
        System.out.println("Initializing Room " + msg.getRoomId());
        messagingTemplate.convertAndSend("/topic/room/Init/"+msg.getRoomId(), msg);
    }
}
