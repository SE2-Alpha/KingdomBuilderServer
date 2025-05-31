package at.aau.serg.kingdombuilderserver.board.quadrants;

import at.aau.serg.kingdombuilderserver.board.Quadrant;
import at.aau.serg.kingdombuilderserver.board.TerrainType;

import java.util.Arrays;

public class QuadrantTower implements Quadrant {
    private static final TerrainType[] MAP = new TerrainType[100];

    static {
        Arrays.fill(MAP, TerrainType.DESERT);
        int[] forest = {0, 1, 2, 3, 10, 12, 13, 22, 85, 86, 94, 95, 96};
        for (int i : forest) MAP[i] = TerrainType.FOREST;
        int[] mountain = {4, 5, 7, 11, 16, 17, 18, 29, 38, 39};
        for (int i : mountain) MAP[i] = TerrainType.MOUNTAIN;
        int[] grass = {6, 15, 26, 27, 36, 46, 58, 68, 78, 79, 88, 89, 97, 98, 99};
        for (int i : grass) MAP[i] = TerrainType.GRASS;
        int[] canyon = {8, 9, 19, 48, 49, 51, 57, 59, 62, 69, 70, 71, 81, 91, 92};
        for (int i : canyon) MAP[i] = TerrainType.CANYON;
        int[] flowers = {14, 20, 21, 23, 24, 25, 31, 32, 33, 44, 66, 75, 76, 77, 87};
        for (int i : flowers) MAP[i] = TerrainType.FLOWERS;
        int[] water = {28, 34, 37, 45, 47, 55, 56, 65, 74, 82, 83, 84, 93};
        for (int i : water) MAP[i] = TerrainType.WATER;
        int[] desert = {30, 40, 41, 42, 43, 50, 52, 53, 54, 60, 61, 63, 64, 73, 80, 90};
        for (int i : desert) MAP[i] = TerrainType.DESERT;
        int[] special = {35, 72};
        for (int i : special) MAP[i] = TerrainType.SPECIALABILITY;
        MAP[67] = TerrainType.CITY;
        // alle anderen bleiben DESERT
    }

    @Override
    public TerrainType getFieldType(int id) {
        if (id >= 0 && id < 100) return MAP[id];
        return TerrainType.DESERT;
    }
}
