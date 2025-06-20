package at.aau.serg.kingdombuilderserver.messaging.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerScoreDTO {
    private String playerId;
    private String playerName;
    private int points;

    public PlayerScoreDTO(String playerId, String playerName, int points) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.points = points;
    }
}

