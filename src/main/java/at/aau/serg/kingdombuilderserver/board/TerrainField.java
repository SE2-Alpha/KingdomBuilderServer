package at.aau.serg.kingdombuilderserver.board;

import at.aau.serg.kingdombuilderserver.game.Player;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TerrainField {
    private TerrainType type;
    private int id;
    private String owner; // Optional, falls ein Spieler das Feld besitzt
    private int ownerSinceRound; // Optional, falls ein Spieler das Feld besitzt


    // ... weitere Methoden, siehe Kotlin

    public TerrainField(TerrainType type, int id) {
        this.type = type;
        this.id = id;
    }
    // Getter/Setter, etc.
}
