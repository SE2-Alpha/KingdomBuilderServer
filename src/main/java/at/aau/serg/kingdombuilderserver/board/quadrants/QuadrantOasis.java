package at.aau.serg.kingdombuilderserver.board.quadrants;

import at.aau.serg.kingdombuilderserver.board.Quadrant;
import at.aau.serg.kingdombuilderserver.board.TerrainType;

import java.util.Arrays;

public class QuadrantOasis implements Quadrant {
    private static final TerrainType[] MAP = new TerrainType[100];

    static {
        Arrays.fill(MAP, TerrainType.WATER);
        int[] forest = {3, 4, 7, 8, 9, 14, 17, 18, 19, 24, 25, 29, 34, 38, 39};
        for (int i : forest) MAP[i] = TerrainType.FOREST;
        int[] mountain = {50, 51, 63, 74, 87};
        for (int i : mountain) MAP[i] = TerrainType.MOUNTAIN;
        int[] grass = {0, 1, 2, 6, 10, 11, 12, 16, 20, 23, 27, 28, 33, 53, 54, 64};
        for (int i : grass) MAP[i] = TerrainType.GRASS;
        int[] canyon = {32, 43, 44, 52, 60, 61, 62, 70, 71, 78, 79, 88, 89, 99};
        for (int i : canyon) MAP[i] = TerrainType.CANYON;
        int[] flowers = {21, 22, 30, 31, 36, 40, 41, 42, 46, 65, 66, 67, 76, 77};
        for (int i : flowers) MAP[i] = TerrainType.FLOWERS;
        int[] water = {5, 15, 26, 35, 45, 48, 49, 55, 56, 57, 80, 81, 82, 90, 91, 92, 93};
        for (int i : water) MAP[i] = TerrainType.WATER;
        int[] desert = {58, 59, 68, 69, 73, 75, 83, 84, 85, 86, 94, 95, 96, 97, 98};
        for (int i : desert) MAP[i] = TerrainType.DESERT;
        MAP[47] = TerrainType.SPECIALABILITY;
        int[] city = {13, 72};
        for (int i : city) MAP[i] = TerrainType.CITY;
        // alle anderen bleiben WATER
    }

    @Override
    public TerrainType getFieldType(int id) {
        if (id >= 0 && id < 100) return MAP[id];
        return TerrainType.WATER;
    }
}
