package at.aau.serg.kingdombuilderserver.board;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerrainFieldTest {
    private TerrainField terrainField;

    //Überprüft ob alle Felder die richtige Anzahl an Nachbarn haben
    @ParameterizedTest
    @MethodSource("fieldnums")
    public void getNeighboursAmountTest(int id) {
        terrainField = new TerrainField(TerrainType.GRASS, id);
        int size = TerrainField.getNeighbours(id).length;

        if (id < 0 || id > 399) {
            assertEquals(1, size);
        } else {
            switch (id) {
                case 0:
                case 399:
                    assertEquals(2, size);
                    break;

                case 19:
                case 380:
                case 40: case 80: case 120: case 160: case 200:
                case 240: case 280: case 320: case 360:
                case 39: case 79: case 119: case 159: case 199:
                case 239: case 279: case 319: case 359:
                    assertEquals(3, size);
                    break;

                default:
                    if ((id >= 1 && id <= 18) || (id >= 381 && id <= 398)) {
                        assertEquals(4, size);
                    } else if (
                            id == 20 || id == 59 || id == 60 || id == 99 || id == 100 || id == 139 ||
                                    id == 140 || id == 179 || id == 180 || id == 219 || id == 220 || id == 259 ||
                                    id == 260 || id == 299 || id == 300 || id == 339 || id == 340 || id == 379
                    ) {
                        assertEquals(5, size);
                    } else {
                        assertEquals(6, size);
                    }
                    break;
            }
        }
    }

    static Stream<Integer> fieldnums() {
        return IntStream.rangeClosed(0, 400).boxed();
    }

}
