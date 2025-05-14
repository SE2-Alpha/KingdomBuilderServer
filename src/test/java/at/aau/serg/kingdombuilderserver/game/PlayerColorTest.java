package at.aau.serg.kingdombuilderserver.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerColorTest {

    @Test
    void getColorSuccessTest(){
        assertEquals(PlayerColor.RED,PlayerColor.getColor(0));
        assertEquals(PlayerColor.BLUE,PlayerColor.getColor(1));
        assertEquals(PlayerColor.WHITE,PlayerColor.getColor(2));
        assertEquals(PlayerColor.BLACK,PlayerColor.getColor(3));
    }

    @Test
    void getColorFailTest(){
        assertEquals(-1,PlayerColor.getColor(4));
        assertEquals(-1,PlayerColor.getColor(5));
        assertEquals(-1,PlayerColor.getColor(-10));
    }
}
