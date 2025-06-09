package at.aau.serg.kingdombuilderserver.messaging.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerScoreDTO {
    private String playerId;
    private int points;

    public PlayerScoreDTO(String playerId, int points) {
        this.playerId = playerId;
        this.points = points;
    }
}

