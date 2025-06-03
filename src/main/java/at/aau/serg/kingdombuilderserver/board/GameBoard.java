package at.aau.serg.kingdombuilderserver.board;

import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantOasis;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantTavern;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantTower;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantFields;
import at.aau.serg.kingdombuilderserver.game.GameHousePosition;
import at.aau.serg.kingdombuilderserver.game.Player;

import java.util.*;
import java.util.function.Supplier;

public class GameBoard {
    private static final int SIZE = 400; // 20x20
    private final TerrainField[] fields = new TerrainField[SIZE];

    private final Random rand = new Random();

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
        List<Quadrant> quadrants = pool.subList(0, 4).stream().map(Supplier::get).toList();

        // 2. Für jeden Quadranten eine Zufallsrotation wählen (0, 90, 180, 270 Grad)
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

    public boolean isPositionValid(GameHousePosition position) {
        if (position == null) {
            return false;
        }
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && x < 20 && y >= 0 && y < 20; // 20x20 Spielfeld
    }

    /*
    TODO():
    - Check Player house count (safety measure)
    -Check placing-type (Normal, Special)
    - Normal House placing:
        - Get required field type (Draw Card)
        - Check field is placeable (includes: Check field is empty)
        - check if required field type is available
            - if Yes: are some of them adjacent to Houses of Player?
                - if yes: make placeable
                - if no: make all fields of required type placeable
            - if No: Make all adjacent fields placeable (respecting general placement rules)
    -Special House placing (Farm, Oracle, Tavern, Tower): Separate function?
            - Farm: Build one additional settlement on Grass (adjacent if possible)
            - Oracle: Build one additional settlement of the same type as required Field type (Draw Card, must be adjacent)
            - Tavern: Build one additional settlement on the end of a straight line of at least 3 settlements
            - Tower: Build one Settlement on the Edge of the Board (adjacent if possible)
     */
    public void placeHouse(Player activePlayer, GameHousePosition position, int round) {
        if (activePlayer == null || position == null || activePlayer.getCurrentCard() == null) {
            throw new IllegalArgumentException("Aktiver Spieler, Position und Karte dürfen nicht null sein.");
        }

        TerrainType currenCard = activePlayer.getCurrentCard();
        String currentPID = activePlayer.getId();

        int id = position.getY() * 20 + position.getX(); // Umrechnung in 1D-Index
        TerrainField field = fields[id];

        if(!field.getType().isBuildable){
            throw new IllegalStateException("Feld kann nicht bebaut werden: " + field);
        }

        List<Integer> freeFields = getFreeFieldsOfType(currenCard);
        List<Integer> buildByActivePlayer = getFieldsBuiltBy(activePlayer.getId());
        List<Integer> freeAdjacentCurrentTypeFields = getAdjacentFields(buildByActivePlayer, freeFields);

        if (field.getOwner() != null) {
            //TODO(): Remove House if: owner is activePlayer and field was built in current round
            throw new IllegalStateException("Feld ist bereits von einem anderen Spieler besetzt: " + field);
        }

        if(!freeFields.isEmpty()) {
            if (field.getType() != currenCard) {
                throw new IllegalStateException("Feld hat falschen FeldTypen: " + field);
            }
        }else{

        }



        field.setOwner(currentPID);
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

    /**
     * Suche alle nachbarfelder in bezug auf ein zentrales Feld
     * @return Liste der Nachbarfelder
     */
    public List<Integer> getAdjacentFields(int center, List<Integer> candidates) {
        List<Integer> adjacentFields = new ArrayList<>();
        for(int candidate: candidates) {
            if(areFieldsAdjacent(fields[center],fields[candidate])){adjacentFields.add(candidate);}
        }
        return adjacentFields;
    }

    /**
     * Suche alle nachbarfelder in bezug auf einer Liste von Feldern
     * @return Liste der Nachbarfelder
     */
    public List<Integer> getAdjacentFields(List<Integer> ownedFields, List<Integer> candidates){
        List<Integer> adjacentFields = new ArrayList<>();
        for(int ownedField : ownedFields) {
            adjacentFields.addAll(getAdjacentFields(ownedField,candidates));
        }
        return adjacentFields;
    }

    /**
     * Id vom einem Feld was man durch row und column kennt
     **/
    public TerrainField getFieldByRowAndCol(int row, int col){
        return fields[row*20 + col];
    }

    /**
     * Überprüfe, ob es für einen Feldtyp freie Felder gibt.
     * @return Liste mit indizes freier Felder (evtl. leere Liste)
     */
    public List<Integer> getFreeFieldsOfType(TerrainType type){
        List<Integer> freeFields = new ArrayList<>();
        for(int i = 0; i < fields.length; i++){
            TerrainField f = fields[i];
            if(f.getType() == type && f.getOwner() == null){freeFields.add(i);}
        }
        return freeFields;
    }

    /**
     * @param id Spieler ID
     * @return Liste der Felder die von Spieler mit id bebaut wurden
     */
    public List<Integer> getFieldsBuiltBy(String id){
        List<Integer> fieldsByPlayer = new ArrayList<>();
        for(int i = 0; i < fields.length; i++){
            if(fields[i].getOwner().equals(id)){fieldsByPlayer.add(i);}
        }
        return fieldsByPlayer;
    }


}
