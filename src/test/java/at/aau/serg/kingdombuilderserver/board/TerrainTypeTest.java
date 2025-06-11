package at.aau.serg.kingdombuilderserver.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class TerrainTypeTest {

    private TerrainType terrainType0;
    private TerrainType terrainType1;
    private TerrainType terrainType2;
    private TerrainType terrainType3;
    private TerrainType terrainType4;
    private TerrainType terrainType5;
    private TerrainType terrainType6;
    private TerrainType terrainType7;
    private TerrainType terrainType8;

    @Test
    void fromIntSuccessTest(){
        terrainType0 = TerrainType.fromInt(0);
        terrainType1 = TerrainType.fromInt(1);
        terrainType2 = TerrainType.fromInt(2);
        terrainType3 = TerrainType.fromInt(3);
        terrainType4 = TerrainType.fromInt(4);
        terrainType5 = TerrainType.fromInt(5);
        terrainType6 = TerrainType.fromInt(6);
        terrainType7 = TerrainType.fromInt(7);
        terrainType8 = TerrainType.fromInt(8);
        assertEquals("TerrainType 0 should be GRASS",TerrainType.GRASS,terrainType0);
        assertEquals("TerrainType 1 should be CANYON",TerrainType.CANYON,terrainType1);
        assertEquals("TerrainType 2 should be DESERT",TerrainType.DESERT,terrainType2);
        assertEquals("TerrainType 3 should be FLOWERS",TerrainType.FLOWERS,terrainType3);
        assertEquals("TerrainType 4 should be FOREST",TerrainType.FOREST,terrainType4);
        assertEquals("TerrainType 5 should be WATER",TerrainType.WATER,terrainType5);
        assertEquals("TerrainType 6 should be MOUNTAIN",TerrainType.MOUNTAIN,terrainType6);
        assertEquals("TerrainType 7 should be SPECIALABILITY",TerrainType.SPECIALABILITY,terrainType7);
        assertEquals("TerrainType 8 should be CITY",TerrainType.CITY,terrainType8);
    }

    @Test
    void fromIntFailTest(){
        assertThrows(IllegalArgumentException.class, () -> terrainType2 = TerrainType.fromInt(9));
        assertThrows(IllegalArgumentException.class, () -> terrainType1 = TerrainType.fromInt(-1));
    }

    @Test
    void toIntTest(){
        terrainType1 = TerrainType.GRASS;
        terrainType2 = TerrainType.WATER;
        terrainType3 = TerrainType.CITY;
        assertEquals("GRASS has ordinal 0",0,terrainType1.toInt());
        assertEquals("WATER has ordinal 5",5,terrainType2.toInt());
        assertEquals("CITY has ordinal 8",8,terrainType3.toInt());
    }
}
