package at.aau.serg.kingdombuilderserver.board.quadrants;

import at.aau.serg.kingdombuilderserver.board.Quadrant;
import at.aau.serg.kingdombuilderserver.board.TerrainType;

import java.util.Arrays;

public class QuadrantFields implements Quadrant {
    private static final TerrainType[] MAP = new TerrainType[100];

    static {
        Arrays.fill(MAP, TerrainType.WATER);
        int[] forest = {5, 6, 7, 14, 15, 16, 26, 72, 73, 82, 83, 90, 91, 92};
        for (int i : forest) MAP[i] = TerrainType.FOREST;
        int[] mountain = {74, 81};
        for (int i : mountain) MAP[i] = TerrainType.MOUNTAIN;
        int[] grass = {8, 9, 18, 19, 41, 42, 50, 51, 60, 61, 62, 70, 71, 80};
        for (int i : grass) MAP[i] = TerrainType.GRASS;
        int[] canyon = {2, 12, 20, 21, 22, 27, 30, 31, 37, 38, 40, 48, 49, 59};
        for (int i : canyon) MAP[i] = TerrainType.CANYON;
        int[] flowers = {23, 24, 25, 28, 29, 32, 33, 39, 44, 45, 53, 55, 63, 65};
        for (int i : flowers) MAP[i] = TerrainType.FLOWERS;
        int[] desert = {0, 1, 10, 35, 36, 46, 47, 57, 58, 68, 69, 78};
        for (int i : desert) MAP[i] = TerrainType.DESERT;
        int[] special = {17, 52};
        for (int i : special) MAP[i] = TerrainType.SPECIALABILITY;
        MAP[11] = TerrainType.CITY;
        // alle anderen bleiben WATER
    }

    @Override
    public TerrainType getFieldType(int id) {
        if (id >= 0 && id < 100) return MAP[id];
        return TerrainType.WATER;
    }
}

