package at.aau.serg.kingdombuilderserver.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
@Getter @Setter
public class Player {
    private @Setter(AccessLevel.NONE) String id;
    private String name;
    private int color;
    private int remainingSettlements;
    private int score;
    public Player(String playerId) {
        this.id = playerId;
        remainingSettlements = 40;
    }

    public Player(String playerId, int initialSettlements){
        this.id = playerId;
        remainingSettlements = initialSettlements;
    }

    public void setRemainingSettlements(int value) {
        if(value < 0){
            remainingSettlements = 0;
        }else{
            remainingSettlements = value;
        }
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
        if(remainingSettlements - Math.abs(value) < 0){
            remainingSettlements = 0;
        }else{
            remainingSettlements -= Math.abs(value);
        }
    }


}
