package at.aau.serg.kingdombuilderserver.board;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerrainFieldTest {

    //Überprüft, ob alle Felder die richtige Anzahl an Nachbarn haben
    @ParameterizedTest
    @MethodSource("fieldnums")
    void getNeighboursAmountTest(int id) {
        int size = TerrainField.getNeighbours(id).length;

        if (id < 0 || id > 399) {
            assertEquals(1, size);
        } else {
            switch (id) {
                case 0, 399:
                    assertEquals(2, size);
                    break;

                case 19, 380, 40, 80, 120, 160, 200, 240, 280, 320, 360, 39, 79, 119, 159, 199, 239, 279, 319, 359:
                    assertEquals(3, size);
                    break;

                default:
                    if (id <= 18 || id >= 381) {
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

    @Test
    void getNeighboursTest() {//Test with corners
        Set<Integer> expected = new HashSet<>();
        List<Integer> fields = new ArrayList<>(Arrays.asList(0, 19, 380, 399));
        List<Integer> actualList = new ArrayList<>(TerrainField.getNeighbours(fields));
        Set<Integer> actual = new HashSet<>(actualList);
        expected.addAll(Arrays.stream(TerrainField.getNeighbours(0)).boxed().toList());
        expected.addAll(Arrays.stream(TerrainField.getNeighbours(19)).boxed().toList());
        expected.addAll(Arrays.stream(TerrainField.getNeighbours(380)).boxed().toList());
        expected.addAll(Arrays.stream(TerrainField.getNeighbours(399)).boxed().toList());
        assertEquals(expected, actual);

    }

    static Stream<Integer> fieldnums() {
        return IntStream.rangeClosed(0, 400).boxed();
    }

}
