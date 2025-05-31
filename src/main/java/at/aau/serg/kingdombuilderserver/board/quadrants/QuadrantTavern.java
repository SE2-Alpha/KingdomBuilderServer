package at.aau.serg.kingdombuilderserver.board.quadrants;

import at.aau.serg.kingdombuilderserver.board.Quadrant;
import at.aau.serg.kingdombuilderserver.board.TerrainType;

import java.util.Arrays;

public class QuadrantTavern implements Quadrant {
    private static final TerrainType[] MAP = new TerrainType[100];

    static {
        Arrays.fill(MAP, TerrainType.FOREST);
        int[] flowers = {0, 10, 11, 20, 21, 22, 23, 24, 25, 26, 32, 40, 41, 50, 61};
        for (int i : flowers) MAP[i] = TerrainType.FLOWERS;
        int[] desert = {1, 2, 5, 6, 12, 13, 14, 60, 70, 71, 80, 81, 82, 90, 91};
        for (int i : desert) MAP[i] = TerrainType.DESERT;
        int[] mountain = {3, 4, 15, 16, 27, 28, 29, 38, 39};
        for (int i : mountain) MAP[i] = TerrainType.MOUNTAIN;
        int[] canyon = {7, 8, 9, 17, 18, 19, 49, 51, 52, 57, 58, 59, 63, 68, 72};
        for (int i : canyon) MAP[i] = TerrainType.CANYON;
        int[] water = {30, 31, 42, 43, 53, 64, 73, 83, 92, 93};
        for (int i : water) MAP[i] = TerrainType.WATER;
        MAP[33] = TerrainType.CITY;
        int[] grass = {34, 35, 44, 45, 46, 54, 69, 76, 77, 78, 79, 87, 88, 89, 97, 98, 99};
        for (int i : grass) MAP[i] = TerrainType.GRASS;
        int[] forest = {36, 37, 47, 48, 55, 56, 65, 66, 74, 75, 84, 85, 86, 94, 95, 96};
        for (int i : forest) MAP[i] = TerrainType.FOREST;
        int[] special = {62, 67};
        for (int i : special) MAP[i] = TerrainType.SPECIALABILITY;
        // alle anderen bleiben FOREST
    }

    @Override
    public TerrainType getFieldType(int id) {
        if (id >= 0 && id < 100) return MAP[id];
        return TerrainType.FOREST;
    }
}
