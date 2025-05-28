package at.aau.serg.kingdombuilderserver.board;

public interface Quadrant {
    TerrainType getFieldType(int id); // id von 0 bis 99 (10x10)
}
