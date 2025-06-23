package at.aau.serg.kingdombuilderserver.messaging.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicMessage {
    private String gameId;
    private String playerId;
}
