package at.aau.serg.kingdombuilderserver.board;

public enum TerrainType {
    GRASS(true),
    CANYON(true),
    DESERT(true),
    FLOWERS(true),
    FOREST(true),
    WATER(false),
    MOUNTAIN(false),
    SPECIALABILITY(false),
    CITY(false);

    public final boolean isBuildable;

    TerrainType(boolean isBuildable) {
        this.isBuildable = isBuildable;
    }
}