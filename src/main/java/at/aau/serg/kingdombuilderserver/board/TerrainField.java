package at.aau.serg.kingdombuilderserver.board;

import lombok.Getter;
import lombok.Setter;

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
                case 20: case 60: case 100: case 140: case 180:
                case 220: case 260: case 300: case 340:
                    return new int[]{id - 20, id - 19, id + 1, id + 20, id + 21};


                //Felder am linken Rand mit 3 Nachbarn
                case 40: case 80: case 120: case 160: case 200:
                case 240: case 280: case 320: case 360:
                    return new int[]{id - 20, id + 1, id + 20};

                //Felder am rechten Rand mit 3 Nachbarn
                case 39: case 79: case 119: case 159: case 199:
                case 239: case 279: case 319: case 359:
                    return new int[]{id - 20, id - 1, id + 20};

                //Felder am rechten Rand mit 5 Nachbarn
                case 59: case 99: case 139: case 179: case 219:
                case 259: case 299: case 339: case 379:
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

    public TerrainField(TerrainType type, int id) {
        this.type = type;
        this.id = id;
    }
    // Getter/Setter, etc.
}
