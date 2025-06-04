package at.aau.serg.kingdombuilderserver.messaging.dtos;

import lombok.Getter;
import lombok.Setter;

public class CheatReportDTO {
    private String gameId;
    private String reporterPlayerId; // ID des Spielers, der den Cheat meldet
    private String reportedPlayerId; // ID des Spielers, der beschuldigt wird (der aktive Spieler)

    @Override
    public String toString() {
        return "CheatReportDTO{" +
                "gameId='" + gameId + '\'' +
                ", reporterPlayerId='" + reporterPlayerId + '\'' +
                ", reportedPlayerId='" + reportedPlayerId + '\'' +
                '}';
    }
}
