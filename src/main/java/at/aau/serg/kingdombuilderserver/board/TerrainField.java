package at.aau.serg.kingdombuilderserver.board;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TerrainField {
    private TerrainType type;
    private int id;

    // ... weitere Methoden, siehe Kotlin

    public TerrainField(TerrainType type, int id) {
        this.type = type;
        this.id = id;
    }
    // Getter/Setter, etc.
}
