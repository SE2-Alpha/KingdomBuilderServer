package at.aau.serg.kingdombuilderserver.board;

import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantOasis;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantTavern;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantTower;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantFields;
import at.aau.serg.kingdombuilderserver.game.GameHousePosition;
import at.aau.serg.kingdombuilderserver.game.Player;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GameBoard {
    private static final int SIZE = 400; // 20x20
    private TerrainField[] fields = new TerrainField[SIZE];

    // Alle verfügbaren Quadranten (hier z.B. 4, erweiterbar)
    private static final List<Supplier<Quadrant>> QUADRANT_SUPPLIERS = List.of(
            QuadrantTower::new,
            QuadrantFields::new,
            QuadrantOasis::new,
            QuadrantTavern::new
            // Weitere Quadranten hinzufügen
    );

    public void buildGameBoard() {
        // 1. Vier verschiedene Quadranten zufällig wählen (ohne Wiederholung)
        List<Supplier<Quadrant>> pool = new ArrayList<>(QUADRANT_SUPPLIERS);
        Collections.shuffle(pool);
        List<Quadrant> quadrants = pool.subList(0, 4).stream().map(Supplier::get).collect(Collectors.toList());

        // 2. Für jeden Quadranten eine Zufallsrotation wählen (0, 90, 180, 270 Grad)
        Random rand = new Random();
        List<Integer> rotations = List.of(rand.nextInt(4), rand.nextInt(4), rand.nextInt(4), rand.nextInt(4));

        // 3. Quadranten "befüllen" und rotieren
        TerrainType[][] bigBoard = new TerrainType[20][20]; // 20x20

        for (int q = 0; q < 4; q++) {
            Quadrant quadrant = quadrants.get(q);

            // Felder des Quadranten in 1D-Array
            TerrainType[] origFields = new TerrainType[100];
            for (int i = 0; i < 100; i++) {
                origFields[i] = quadrant.getFieldType(i);
            }

            // Rotieren
            TerrainType[] rotated = QuadrantUtils.rotateQuadrant(origFields, rotations.get(q));

            // Start-Position auf dem Spielfeld (oben links, oben rechts, unten links, unten rechts)
            int startRow = (q / 2) * 10;
            int startCol = (q % 2) * 10;

            // Auf das große Spielfeld kopieren
            for (int r = 0; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    bigBoard[startRow + r][startCol + c] = rotated[r * 10 + c];
                }
            }
        }

        // 4. TerrainField-Objekte anlegen
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 20; c++) {
                int id = r * 20 + c;
                fields[id] = new TerrainField(bigBoard[r][c], id);
            }
        }
    }

    // Getter für das Feld-Array, etc.
    public TerrainField[] getFields() {
        return fields;
    }

    public boolean isPositionValid(GameHousePosition position) {
        if (position == null) {
            return false;
        }
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && x < 20 && y >= 0 && y < 20; // 20x20 Spielfeld
    }

    public void placeHouse(Player activePlayer, GameHousePosition position, int round) {
        if (activePlayer == null || position == null) {
            throw new IllegalArgumentException("Aktiver Spieler und Position dürfen nicht null sein.");
        }

        int id = position.getY() * 20 + position.getX(); // Umrechnung in 1D-Index
        TerrainField field = fields[id];

        if (field.getOwner() != null) {
            throw new IllegalStateException("Feld ist bereits von einem anderen Spieler besetzt: " + field);
        }

        field.setOwner(activePlayer.getId());
        field.setOwnerSinceRound(round); // Setze die aktuelle Runde als Besitzrunde
    }

    /**
     * Prüft, ob zwei Felder benachbart sind
     * @param field1 Erstes Feld
     * @param field2 Zweites Feld
     */
    public boolean areFieldsAdjacent(TerrainField field1, TerrainField field2) {
        int[] neighbours = TerrainField.getNeighbours(field1.getId()); //hole alle Nachbarn vom field1

        for(int neighbour : neighbours) {
            if (field2.getId() == neighbour) {
                return true;
            }
        }
        return false;
    }


}
