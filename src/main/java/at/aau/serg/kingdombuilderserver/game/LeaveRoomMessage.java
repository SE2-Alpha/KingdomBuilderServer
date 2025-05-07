package at.aau.serg.kingdombuilderserver.game;

import lombok.Getter;
import lombok.Setter;

    @Setter
    @Getter
    public class LeaveRoomMessage {
        private String roomId;
        private String playerId;

    }
