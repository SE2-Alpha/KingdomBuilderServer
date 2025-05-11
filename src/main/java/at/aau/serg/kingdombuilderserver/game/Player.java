package at.aau.serg.kingdombuilderserver.game;

public class Player {
    private String id;
    private String name;
    private int color;
    private int remainingSettlements;
    private int score;
    public Player(String creatorId) {
        this.id = creatorId;
    }

    public Object getId() {
        return id;
    }
}
