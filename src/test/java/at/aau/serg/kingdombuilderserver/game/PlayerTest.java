package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;

    @Test
    void constructor1Test(){
        player = new Player("12345","Playername");
        assertEquals("12345",player.getId());
        assertEquals(40,player.getRemainingSettlements());
    }

    @Test
    void constructor2Test(){
        player = new Player("12345", 10);
        assertEquals("12345",player.getId());
        assertEquals(10,player.getRemainingSettlements());
    }

    @Test
    void setRemainingSettlementsTest(){
        player = new Player("12345","Playername");
        player.setRemainingSettlements(400);
        assertEquals(400,player.getRemainingSettlements());
    }

    @Test
    void setRemainingSettlementsFailTest(){
        player = new Player("12345","Playername");
        player.setRemainingSettlements(-2);
        assertEquals(0,player.getRemainingSettlements());
    }

    @Test
    void increaseSettlementsByTest(){
        player = new Player("12345",0);
        player.increaseSettlementsBy(10);
        assertEquals(10,player.getRemainingSettlements());
        player.increaseSettlementsBy(-10);
        assertEquals(20,player.getRemainingSettlements());
    }

    @Test
    void decreaseSettlementsByTest(){
        player = new Player("12345",50);
        player.decreaseSettlementsBy(10);
        assertEquals(40,player.getRemainingSettlements());
        player.decreaseSettlementsBy(-30);
        assertEquals(10,player.getRemainingSettlements());
    }

    @Test
    void testGoldManagement(){
        player = new Player("p1", "Goldfinger");
        assertEquals(0, player.getGold());
        player.addGold(10);
        assertEquals(10, player.getGold());
        player.decreaseGold(3);
        assertEquals(7, player.getGold());
    }

    @Test
    void testSkippedTurnFlag(){
        player = new Player("p1", "Skipper");
        assertFalse(player.isSkippedTurn());
        player.setSkippedTurn(true);
        assertTrue(player.isSkippedTurn());
    }


}
