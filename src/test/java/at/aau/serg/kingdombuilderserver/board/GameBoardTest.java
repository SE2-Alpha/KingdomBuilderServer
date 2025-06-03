package at.aau.serg.kingdombuilderserver.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {
    private GameBoard gameBoardTest;
    private TerrainField field1;
    private TerrainField field2;
    private TerrainField field3;
    private TerrainField field4;

    @BeforeEach
    void setUp(){
        gameBoardTest = new GameBoard();
        gameBoardTest.buildGameBoard();
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
        TerrainField field1 = gameBoardTest.getFieldByRowAndCol(0,0);
        TerrainField field2 = gameBoardTest.getFieldByRowAndCol(0,1);
        assertTrue(gameBoardTest.areFieldsAdjacent(field1,field2));
    }

    @Test
    void testNonAdjacentFields() {
        TerrainField field1 = gameBoardTest.getFieldByRowAndCol(0,0);
        TerrainField field2 = gameBoardTest.getFieldByRowAndCol(2,2);
        assertFalse(gameBoardTest.areFieldsAdjacent(field1,field2));
    }


    @Test
    void testAdjacentFieldsAcrossQuadrants() {
        TerrainField lastFieldQuadrant1 = gameBoardTest.getFieldByRowAndCol(9, 9);

        TerrainField firstFieldQuadrant2 = gameBoardTest.getFieldByRowAndCol(10, 10);


        System.out.printf("Field 1 ID: %d, Position: (%d, %d)", lastFieldQuadrant1.getId(), lastFieldQuadrant1.getId() % 20, lastFieldQuadrant1.getId() / 20);
        System.out.printf("\nField 2 ID: %d, Position: (%d, %d)", firstFieldQuadrant2.getId(), firstFieldQuadrant2.getId() % 20, firstFieldQuadrant2.getId() / 20);

        assertTrue(
                gameBoardTest.areFieldsAdjacent(lastFieldQuadrant1, firstFieldQuadrant2),
                "Felder an Quadrant-Grenzen sollten als benachbart gelten"
        );
    }

    @Test
    void testSpecificAdjacentPositions() {
        // Horizontale Nachbarn
        TerrainField field1 = gameBoardTest.getFieldByRowAndCol(5, 5);
        TerrainField field2 = gameBoardTest.getFieldByRowAndCol(5, 6);
        assertTrue(gameBoardTest.areFieldsAdjacent(field1, field2));

        // Vertikale Nachbarn (versetzt)
        TerrainField field3 = gameBoardTest.getFieldByRowAndCol(5, 5);
        TerrainField field4 = gameBoardTest.getFieldByRowAndCol(6, 5);
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

}
