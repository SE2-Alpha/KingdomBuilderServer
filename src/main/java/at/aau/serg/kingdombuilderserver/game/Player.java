package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import at.aau.serg.kingdombuilderserver.board.TerrainType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Player {
    @Setter(AccessLevel.NONE)
    private String id;
    private String name;
    private int color;
    private int remainingSettlements;
    private int score;
    private Set<Integer> houseFieldIds = new HashSet<>();
    private boolean hasCheated;
    private boolean skippedTurn = false;
    private int cheatPoints = 0;//Points gained/lost when cheating/reporting

    private TerrainType currentCard = null; //Card pulled in current turn

    public Player(String playerId, String playerName) {
        this.id = playerId;
        this.name = playerName;
        remainingSettlements = 40;
        hasCheated = false;
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

    public void updateHouseFields(GameBoard board) {
        houseFieldIds = new HashSet<>(board.getFieldsBuiltBy(this.id));
    }

    public boolean getHasCheated() {
        return hasCheated;
    }
    public void addCheatPoints(int amount) { this.cheatPoints += amount; }

    public void decreaseCheatPoints(int amount) { this.cheatPoints -= amount; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Color: " + color + "Settlements: " + remainingSettlements;
    }
}
