package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StartRoomMessageTest {
    private Room room;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp(){
        room = new Room("12345","New Room");
        room.addPlayer(player1);
        room.addPlayer(player2);
        room.addPlayer(player3);
        RoomList.list.put(room.getId(), room);
    }

    @Test
    void getterTest(){
        StartRoomMessage startRoomMessage = new StartRoomMessage(room.getId());
        assertEquals("12345", startRoomMessage.getRoomId());
        assertEquals(3, startRoomMessage.getPlayers().size());
    }

    @Test
    void toStringTest(){
        StartRoomMessage startRoomMessage = new StartRoomMessage(room.getId());
        assertInstanceOf(String.class, startRoomMessage.toString());
    }

    @AfterEach
    void tearDown(){
        RoomList.list.remove(room.getId());
        room = null;
    }

}
