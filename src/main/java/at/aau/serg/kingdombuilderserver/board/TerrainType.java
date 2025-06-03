package at.aau.serg.kingdombuilderserver.board;

public enum TerrainType {
    GRASS(true,1),
    CANYON(true,2),
    DESERT(true,3),
    FLOWERS(true,4),
    FOREST(true,5),
    WATER(false,6),
    MOUNTAIN(false,7),
    SPECIALABILITY(false,8),
    CITY(false,9);

    public final boolean isBuildable;
    public final int index;

    TerrainType(boolean isBuildable, int index) {
        this.isBuildable = isBuildable;
        this.index = index;
    }

    public static TerrainType fromInt(int value) {
        for (TerrainType type : TerrainType.values()) {
            if (type.ordinal() == value) {return type;}
        }
        throw new IllegalArgumentException("Unknown terrain type: " + value);
    }

    public int toInt(){
        return this.ordinal();
    }
}