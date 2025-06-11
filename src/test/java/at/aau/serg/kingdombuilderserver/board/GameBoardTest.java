package at.aau.serg.kingdombuilderserver.board;

import at.aau.serg.kingdombuilderserver.game.GameHousePosition;
import at.aau.serg.kingdombuilderserver.game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    private GameBoard gameBoardTest;
    private TerrainField field;
    private TerrainField field1;
    private TerrainField field2;
    private TerrainField field3;
    private TerrainField field4;
    private Player player;
    private List<Integer> list;


    @BeforeEach
    void setUp(){
        gameBoardTest = new GameBoard();
        gameBoardTest.buildGameBoard();
        player = new Player("123","Player");
        list = new ArrayList<>();
    }

    @AfterEach
    void tearDown(){
        gameBoardTest = null;
        player = null;
        list = null;
    }

    @Test
    void areFieldsAdjacentTrueTest(){
        field1 = new TerrainField(TerrainType.MOUNTAIN,21);
        field2 = new TerrainField(TerrainType.FOREST,42);
        field3 = new TerrainField(TerrainType.FLOWERS,41);
        field4 = new TerrainField(TerrainType.FLOWERS,61);

        assertTrue(gameBoardTest.areFieldsAdjacent(field1,field2));
        assertTrue(gameBoardTest.areFieldsAdjacent(field3, field4));
    }
    @Test
    void areFieldsAdjacentFalseTest(){
        field1 = new TerrainField(TerrainType.MOUNTAIN,21);
        field2 = new TerrainField(TerrainType.FLOWERS,40);
        field3 = new TerrainField(TerrainType.FLOWERS,41);
        field4 = new TerrainField(TerrainType.FLOWERS,62);

        assertFalse(gameBoardTest.areFieldsAdjacent(field1,field2));
        assertFalse(gameBoardTest.areFieldsAdjacent(field3, field4));
    }

    //Test Field Placements using getFieldByRowAndCol() - Upper Left, Upper Right, Lower Left, Lower Right
    @Test
    void upperLeftCornerTest(){
        field1 = gameBoardTest.getFieldByRowAndCol(0,0);//Upper left corner

        assertEquals(0,field1.getId());


    }
    @Test
    void upperRightCornerTest(){
        field2 = gameBoardTest.getFieldByRowAndCol(0,19);//Upper right corner

        assertEquals(19,field2.getId());
    }

    @Test
    void lowerLeftCornerTest(){
        field3 = gameBoardTest.getFieldByRowAndCol(19,0);//Lower left corner

        assertEquals(380,field3.getId());
    }

    @Test
    void lowerRightCornerTest(){
        field4 = gameBoardTest.getFieldByRowAndCol(19,19);//Lower right corner

        assertEquals(399,field4.getId());
    }

    @Test
    void testAdjacentFields() {
        field1 = gameBoardTest.getFieldByRowAndCol(0,0);
        field2 = gameBoardTest.getFieldByRowAndCol(0,1);
        assertTrue(gameBoardTest.areFieldsAdjacent(field1,field2));
    }

    @Test
    void testNonAdjacentFields() {
        field1 = gameBoardTest.getFieldByRowAndCol(0,0);
        field2 = gameBoardTest.getFieldByRowAndCol(2,2);
        assertFalse(gameBoardTest.areFieldsAdjacent(field1,field2));
    }


    @Test
    void testAdjacentFieldsAcrossQuadrants() {
        TerrainField lastFieldQuadrant1 = gameBoardTest.getFieldByRowAndCol(9, 9);

        TerrainField firstFieldQuadrant2 = gameBoardTest.getFieldByRowAndCol(10, 10);


        System.out.printf("Field 1 ID: %d, Position: (%d, %d)\n", lastFieldQuadrant1.getId(), lastFieldQuadrant1.getId() % 20, lastFieldQuadrant1.getId() / 20);
        System.out.printf("Field 2 ID: %d, Position: (%d, %d)", firstFieldQuadrant2.getId(), firstFieldQuadrant2.getId() % 20, firstFieldQuadrant2.getId() / 20);

        assertTrue(
                gameBoardTest.areFieldsAdjacent(lastFieldQuadrant1, firstFieldQuadrant2),
                "Felder an Quadrant-Grenzen sollten als benachbart gelten"
        );
    }

    @Test
    void testSpecificAdjacentPositions() {
        // Horizontale Nachbarn
        field1 = gameBoardTest.getFieldByRowAndCol(5, 5);
        field2 = gameBoardTest.getFieldByRowAndCol(5, 6);
        assertTrue(gameBoardTest.areFieldsAdjacent(field1, field2));

        // Vertikale Nachbarn (versetzt)
        field3 = gameBoardTest.getFieldByRowAndCol(5, 5);
        field4 = gameBoardTest.getFieldByRowAndCol(6, 5);
        assertTrue(gameBoardTest.areFieldsAdjacent(field3, field4));

        // Diagonale Nachbarn
        TerrainField field5 = gameBoardTest.getFieldByRowAndCol(5, 5);
        TerrainField field6 = gameBoardTest.getFieldByRowAndCol(6, 6);
        assertTrue(gameBoardTest.areFieldsAdjacent(field5, field6));

        // Nicht Nachbarn
        TerrainField field7 = gameBoardTest.getFieldByRowAndCol(0, 0);
        TerrainField field8 = gameBoardTest.getFieldByRowAndCol(5, 5);
        assertFalse(gameBoardTest.areFieldsAdjacent(field7, field8));
    }

    @Test
    void isPositionValidTrueTest(){
        GameHousePosition pos1 = new GameHousePosition(15,15);
        GameHousePosition pos2 = new GameHousePosition(0,12);
        assertTrue(gameBoardTest.isPositionValid(pos1));
        assertTrue(gameBoardTest.isPositionValid(pos2));
    }

    @Test
    void isPositionValidFalseTest(){
        GameHousePosition pos1 = new GameHousePosition(21,40);
        GameHousePosition pos2 = new GameHousePosition(-1,-1);
        assertFalse(gameBoardTest.isPositionValid(pos1));
        assertFalse(gameBoardTest.isPositionValid(pos2));
    }

    @Test
    void isPositionValidNullTest(){
        GameHousePosition pos1 = null;
        assertFalse(gameBoardTest.isPositionValid(pos1));
    }

    @Test
    void placeLegallyTest(){
        field  = gameBoardTest.getFieldByRowAndCol(1,1); //ID 21 corresponds to (1,1)
        gameBoardTest.placeLegally(field,player,5,list);
        assertEquals(21, field.getId());
        assertEquals(player.getId(),gameBoardTest.getFieldByRowAndCol(1,1).getOwner());
        assertEquals(5,gameBoardTest.getFieldByRowAndCol(1,1).getOwnerSinceRound());
        assertTrue(list.contains(field.getId()));
    }

    @Test
    void removeLegallySuccessTest(){
        field  = gameBoardTest.getFieldByRowAndCol(1,1); //ID 21 corresponds to (1,1)
        gameBoardTest.placeLegally(field,player,5,list);
        gameBoardTest.removeLegally(field,list,player);
        assertNull(field.getOwner());
        assertEquals(-1,field.getOwnerSinceRound());
        assertFalse(list.contains(field.getId()));
    }

    @Test
    void removeLegallyFailTest(){
        field  = gameBoardTest.getFieldByRowAndCol(1,1); //ID 21 corresponds to (1,1)
        gameBoardTest.placeLegally(field,player,5,list);
        list.clear();
        assertThrows(RuntimeException.class, () -> gameBoardTest.removeLegally(field,list,player));

    }

    @Test
    void placeHouseFails1Test(){//Fails at null check
        player.setCurrentCard(TerrainType.MOUNTAIN);
        GameHousePosition pos = null;
        assertThrows(IllegalArgumentException.class, () -> gameBoardTest.placeHouse(player,list,pos,5));
    }
    @Test
    void placeHouseFails2Test(){//Fails at invalid position
        player.setCurrentCard(TerrainType.MOUNTAIN);
        GameHousePosition pos = new GameHousePosition(40,30);
        assertThrows(IllegalArgumentException.class, () -> gameBoardTest.placeHouse(player,list,pos,5));
    }

    @Test
    void placeHouseFails3Test(){//Fails at field already full
        GameHousePosition pos = new GameHousePosition(1,1);
        field = gameBoardTest.getFieldByRowAndCol(1,1);
        field.setOwner("123123123");
        field.setType(TerrainType.GRASS);
        player.setCurrentCard(field.getType());
        assertThrows(IllegalStateException.class, () -> gameBoardTest.placeHouse(player,list,pos,5));
    }

    @Test
    void placeHouseFails4Test(){//Fails at Field not buildable
        GameHousePosition pos = new GameHousePosition(1,1);
        field = gameBoardTest.getFieldByRowAndCol(1,1);
        field.setType(TerrainType.SPECIALABILITY);
        player.setCurrentCard(field.getType());
        assertThrows(IllegalStateException.class, () -> gameBoardTest.placeHouse(player,list,pos,5));
    }

    @Test
    void placeHouseFails5Test(){//Fails on Settlement count
        GameHousePosition pos = new GameHousePosition(1,1);
        field = gameBoardTest.getFieldByRowAndCol(1,1);
        field.setType(TerrainType.GRASS);
        player.setRemainingSettlements(0);
        player.setCurrentCard(field.getType());
        assertThrows(IllegalArgumentException.class, () -> gameBoardTest.placeHouse(player,list,pos,5));
    }

    @Test
    void placeHouseFails6Test(){//Fail on 3 buildings placed
        GameHousePosition pos = new GameHousePosition(1,1);
        field = gameBoardTest.getFieldByRowAndCol(1,1);
        field.setType(TerrainType.GRASS);
        list.addAll(Arrays.asList(0,1,2));
        assertThrows(IllegalArgumentException.class, () -> gameBoardTest.placeHouse(player,list,pos,5));
    }

    @Test
    void placeHouseFails7Test(){//Fail on Wrong Type
        GameHousePosition pos = new GameHousePosition(1,1);
        field = gameBoardTest.getFieldByRowAndCol(1,1);
        field.setType(TerrainType.GRASS);
        player.setCurrentCard(TerrainType.FOREST);
        assertThrows(IllegalStateException.class, () -> gameBoardTest.placeHouse(player,list,pos,5));
    }

    @Test
    void placeHouseSuccess1Test(){//Success Removing house
        GameHousePosition pos = new GameHousePosition(1,1);
        field = gameBoardTest.getFieldByRowAndCol(1,1);
        field.setType(TerrainType.GRASS);
        field.setOwner(player.getId());
        field.setOwnerSinceRound(3);
        player.setCurrentCard(field.getType());
        list.add(field.getId());
        gameBoardTest.placeHouse(player,list,pos,3);
        assertNull(field.getOwner());
        assertEquals(-1,field.getOwnerSinceRound());
    }

    @Test
    void placeHouseSuccess2Test(){//Success Placing house
        GameHousePosition pos = new GameHousePosition(5,5);
        field =  gameBoardTest.getFieldByRowAndCol(5,5);
        field.setType(TerrainType.GRASS);
        player.setCurrentCard(field.getType());
        assertDoesNotThrow(() -> gameBoardTest.placeHouse(player,list,pos,5));

    }

    @Test
    void getAdjacentFields1Test(){//Parameters int + List
        list.add(5);
        List<Integer> list2 = gameBoardTest.getAdjacentFields(4,list);
        assertTrue(list2.contains(5));
    }

}
