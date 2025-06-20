package at.aau.serg.kingdombuilderserver.dto;

import at.aau.serg.kingdombuilderserver.messaging.dtos.CheatReportDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CheatReportDTOTest {
    @Test
    void testGettersAndSetters() {
        CheatReportDTO dto = new CheatReportDTO();
        String gameId = "game123";
        String reporterId = "player1";
        String reportedId = "player2";

        dto.setGameId(gameId);
        dto.setReporterPlayerId(reporterId);
        dto.setReportedPlayerId(reportedId);

        assertEquals(gameId, dto.getGameId());
        assertEquals(reporterId, dto.getReporterPlayerId());
        assertEquals(reportedId, dto.getReportedPlayerId());
    }

    @Test
    void testToString() {
        CheatReportDTO dto = new CheatReportDTO();
        dto.setGameId("game123");
        dto.setReporterPlayerId("p1");
        dto.setReportedPlayerId("p2");

        String expected = "CheatReportDTO{" +
                "gameId='game123'," +
                " reporterPlayerId='p1'," +
                " reportedPlayerId='p2'" +
                '}';

        assertEquals(expected.replaceAll("\\s+", ""), dto.toString().replaceAll("\\s+", ""));
    }
}
