package at.aau.serg.kingdombuilderserver.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class Player {
    private @Setter(AccessLevel.NONE) String id;
    private String name;
    private int color;
    private int remainingSettlements;
    private int score;
    private Set<Integer> houseFieldIds = new HashSet<>();

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

    // Haus auf ein Feld setzen
    public boolean placeHouse(int fieldId) {
        return houseFieldIds.add(fieldId); // Gibt false zurück, wenn schon gesetzt
    }

    // Prüfen ob ein Haus auf dem Feld steht
    public boolean hasHouseOnField(int fieldId) {
        return houseFieldIds.contains(fieldId);
    }

}
