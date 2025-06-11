package at.aau.serg.kingdombuilderserver.board;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class TerrainField {
    private TerrainType type;
    private int id;
    private String owner; // Optional, falls ein Spieler das Feld besitzt
    private int ownerSinceRound; // Optional, falls ein Spieler das Feld besitzt


    // ... weitere Methoden, siehe Kotlin

    /**
     * @return Gibt alle Nachbarn in einem Array zurück. Falls die Id nicht zwischen 0 und 399 liegt, gibt es -1 zurück
     */
    public static int[] getNeighbours(int id) {
        if (id > -1 && id < 400) {
            switch (id) {
                //Felder am linken Rand mit 5 Nachbarn
                case 20, 60, 100, 140, 180, 220, 260, 300, 340:
                    return new int[]{id - 20, id - 19, id + 1, id + 20, id + 21};


                //Felder am linken Rand mit 3 Nachbarn
                case 40, 80, 120, 160, 200, 240, 280, 320, 360:
                    return new int[]{id - 20, id + 1, id + 20};

                //Felder am rechten Rand mit 3 Nachbarn
                case 39, 79, 119, 159, 199, 239, 279, 319, 359:
                    return new int[]{id - 20, id - 1, id + 20};

                //Felder am rechten Rand mit 5 Nachbarn
                case 59, 99, 139, 179, 219, 259, 299, 339, 379:
                    return new int[]{id - 21, id - 20, id - 1, id + 19, id + 20};

                //Ecke links oben
                case 0:
                    return new int[]{1, 21};

                //Ecke rechts oben
                case 19:
                    return new int[]{18, 38, 39};

                //Ecke links unten
                case 380:
                    return new int[]{360, 361, 381};

                //Ecke rechts unten
                case 399:
                    return new int[]{379, 398};

                default:
                    if ((id <= 18)) { //Rand oben
                        return new int[]{id - 1, id + 1, id + 19, id + 20};
                    } else if (id >= 381) { //Rand unten
                        return new int[]{id - 1, id + 1, id - 19, id - 20};
                    } else if ((id <= 38) || (id >= 61 && id <= 78) ||
                            (id >= 101 && id <= 118) || (id >= 141 && id <= 158) ||
                            (id >= 181 && id <= 198) || (id >= 221 && id <= 238) ||
                            (id >= 261 && id <= 278) || (id >= 301 && id <= 318) ||
                            (id >= 341)) { //Hälfte der Felder
                        return new int[]{id - 20, id - 19, id - 1, id + 1, id + 20, id + 21};
                    } else { //alle restlichen Felder (andere hälfte)
                        return new int[]{id - 21, id - 20, id - 1, id + 1, id + 19, id + 20};
                    }
            }
        }
        return new int[]{-1};
    }

    /**
     *
     * @param ids list of Field IDs
     * @return List of unique neighbours of all ids
     */
    public static List<Integer> getNeighbours(List<Integer> ids) {
        HashSet<Integer> neighbours = new HashSet<>();
        for(int id : ids) {
            neighbours.addAll(Arrays.stream(getNeighbours(id)).boxed().toList());
        }
        return neighbours.stream().toList();
    }


    public TerrainField(TerrainType type, int id) {
        this.type = type;
        this.id = id;
    }
    // Getter/Setter, etc.
}
