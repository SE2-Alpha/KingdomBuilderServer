package at.aau.serg.kingdombuilderserver.game;

public class CreateRoomMessage {
    private String playerId;

    // Standard-Konstruktor
    public CreateRoomMessage() { }

    // Getter und Setter
    public String getPlayerId() {
        return playerId;
    }
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
