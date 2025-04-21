package at.aau.serg.websocketdemoserver.game;

public class Player {
    private String id;
    public Player(String creatorId) {
        this.id = creatorId;
    }

    public Object getId() {
        return id;
    }
}
