package at.aau.serg.kingdombuilderserver.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Player {
    private @Setter(AccessLevel.NONE) String id;
    private String name;
    private int color;
    private int remainingSettlements;
    private int score;
    private boolean hasCheated = false;
    private boolean skippedTurn = false;
    private int gold = 0;
    private List<GameHousePosition> housesPlacedThisTurn = new ArrayList<>();


    public Player(String playerId, String playerName) {
        this.id = playerId;
        this.name = playerName;
        remainingSettlements = 40;
    }

    public Player(String playerId, int initialSettlements){
        this.id = playerId;
        remainingSettlements = initialSettlements;
    }

    public void setRemainingSettlements(int value) {
        remainingSettlements = Math.max(value, 0);
    }

    /**
     *Strictly increases settlement count by the absolute value of the parameter.
     */
    public void increaseSettlementsBy(int value){
        remainingSettlements += Math.abs(value);
    }

    /**
     *Strictly decreases settlement count by the absolute value of the parameter.
     */
    public void decreaseSettlementsBy(int value){
        remainingSettlements = Math.max(remainingSettlements - Math.abs(value), 0);
    }

    public boolean hasCheated() {
        return hasCheated;
    }

    public List <GameHousePosition> getHousePlacedThisTurn(){
        return housesPlacedThisTurn;
    }

}
