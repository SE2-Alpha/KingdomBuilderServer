package at.aau.serg.kingdombuilderserver.game;

public class CreateRoomMessage {
    private String playerId;

    /**
     * Default constructor for CreateRoomMessage.
     * Currently, this constructor is not intended to be used.
     * If called, it will throw an UnsupportedOperationException.
     */
    public CreateRoomMessage() {
        throw new UnsupportedOperationException("Default constructor is not supported.");
    }

    // Getter und Setter
    public String getPlayerId() {
        return playerId;
    }
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
