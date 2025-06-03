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

    public static TerrainType fromInt(int value) {
        return switch (value) {
            case 0 -> GRASS;
            case 1 -> CANYON;
            case 2 -> DESERT;
            case 3 -> FLOWERS;
            case 4 -> FOREST;
            case 5 -> WATER;
            case 6 -> MOUNTAIN;
            case 7 -> SPECIALABILITY;
            case 8 -> CITY;
            default -> throw new IllegalArgumentException("Unknown terrain type: " + value);
        };
    }

    public int toInt(){
        return this.ordinal()+1;
    }
}