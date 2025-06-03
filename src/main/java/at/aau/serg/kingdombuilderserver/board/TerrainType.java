package at.aau.serg.kingdombuilderserver.board;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
       switch (value){
           case 0: return GRASS;
           case 1: return CANYON;
           case 2: return DESERT;
           case 3: return FLOWERS;
           case 4: return FOREST;
           case 5: return WATER;
           case 6: return MOUNTAIN;
           case 7: return SPECIALABILITY;
           case 8: return CITY;
           default: throw new IllegalArgumentException("Unknown terrain type: " + value);
       }
    }

    public int toInt(){
        return this.ordinal()+1;
    }
}