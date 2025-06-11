package at.aau.serg.kingdombuilderserver.board;

import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantFields;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantOasis;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantTavern;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantTower;
import at.aau.serg.kingdombuilderserver.game.GameHousePosition;
import at.aau.serg.kingdombuilderserver.game.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class GameBoard {
    private static final int SIZE = 400; // 20x20
    @Getter
    private final TerrainField[] fields = new TerrainField[SIZE];

    @JsonIgnore
    private Random rand = new Random();

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
    -Special House placing (Farm, Oracle, Tavern, Tower): Separate function?
            - Farm: Build one additional settlement on Grass (adjacent if possible)
            - Oracle: Build one additional settlement of the same type as required Field type (Draw Card, must be adjacent)
            - Tavern: Build one additional settlement on the end of a straight line of at least 3 settlements
            - Tower: Build one Settlement on the Edge of the Board (adjacent if possible)
     */
    public void placeHouse(Player activePlayer,List<Integer> activeList, GameHousePosition position, int round) {
        if (activePlayer == null || position == null || activePlayer.getCurrentCard() == null) {
            int errcode = 1000;
            if(activePlayer == null) errcode +=1; else if(activePlayer.getCurrentCard() == null) errcode +=100;
            if(position == null) errcode +=10;

            throw new IllegalArgumentException("Aktiver Spieler, Position und Karte dürfen nicht null sein. " + errcode);
        }

        TerrainType currentCard = activePlayer.getCurrentCard();
        String currentPID = activePlayer.getId();

        int id = position.getY() * 20 + position.getX(); // Umrechnung in 1D-Index
        TerrainField field = fields[id];

        if(!field.getType().isBuildable){
            throw new IllegalStateException("Feld kann nicht bebaut werden: " + field);
        }

        if (field.getOwner() != null) {
            if(field.getOwner().equals(currentPID) && field.getOwnerSinceRound() == round){
                removeLegally(field,activeList);
                return;
            }else{
                throw new IllegalStateException("Feld ist bereits von einem anderen Spieler besetzt: " + field);
            }
        }

        List<Integer> freeFieldsOfCurrentType = getFreeFieldsOfType(currentCard);
        List<Integer> builtByActivePlayer = getFieldsBuiltBy(currentPID);
        List<Integer> allFreeAdjacentFields = getAdjacentFields(builtByActivePlayer);
        List<Integer> freeAdjacentCurrentTypeFields = getAdjacentFields(builtByActivePlayer, freeFieldsOfCurrentType);


        if(freeFieldsOfCurrentType.isEmpty()){//TODO(): Check available Fields when pulling Card
            throw new IllegalStateException("Es Existiert kein freies Feld mit richtigen Typen: " + field);
        }

        //First building
        if(builtByActivePlayer.isEmpty() && freeFieldsOfCurrentType.contains(id)
        ){
            placeLegally(field,currentPID,round,activeList);
            return;
        }

        if(
                freeAdjacentCurrentTypeFields.contains(id) ||
                freeAdjacentCurrentTypeFields.isEmpty() &&
                (freeFieldsOfCurrentType.contains(id))
        ){
            placeLegally(field,currentPID,round,activeList);
            return;
        }

        if(field.getType() != currentCard) {
            throw new IllegalStateException("Feld hat falschen FeldTypen: " + field.getType() + " statt "+ currentCard);
        }


        if(!freeAdjacentCurrentTypeFields.isEmpty()  && !freeAdjacentCurrentTypeFields.contains(id)
                || !allFreeAdjacentFields.isEmpty() && !allFreeAdjacentFields.contains(id)
        ){
            throw new IllegalStateException("Feld an nicht erlaubter Position: " + field.getId()+ " "
                    +freeAdjacentCurrentTypeFields + " " + allFreeAdjacentFields );
        }

        //Failsafe
        throw new IllegalStateException("Feld konnte nicht bebaut werden: " + field);
    }

    public void placeLegally(TerrainField field, String PlayerId, int round,List<Integer> buffer){
        field.setOwner(PlayerId);
        field.setOwnerSinceRound(round);
        buffer.add(field.getId());
    }

    public void removeLegally(TerrainField field,List<Integer> buffer){
        int index = buffer.indexOf(field.getId());
        if(index == -1) {
            throw new RuntimeException("Field "+ field +" not in buffer array " + buffer);
        }
        List<Integer> choppingBlock = buffer.subList(index,buffer.size());
        for(Integer f : choppingBlock){
            fields[f].setOwner(null);
            fields[f].setOwnerSinceRound(-1);
        }
        buffer.subList(index,buffer.size()).clear();
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
     * Suche alle Nachbarfelder in bezug auf ein zentrales Feld
     * @return ID-Liste der Nachbarfelder
     */
    public List<Integer> getAdjacentFields(int center, List<Integer> candidates) {
        List<Integer> adjacentFields = new ArrayList<>();
        for(int candidate: candidates) {
            if(areFieldsAdjacent(fields[center],fields[candidate])){adjacentFields.add(candidate);}
        }
        return adjacentFields;
    }

    /**
     * Suche alle Nachbarfelder in bezug auf einer Liste von Feldern
     * @return ID-Liste der Nachbarfelder
     */
    public List<Integer> getAdjacentFields(List<Integer> ownedFields, List<Integer> candidates){
        List<Integer> adjacentFields = new ArrayList<>();
        for(int ownedField : ownedFields) {
            adjacentFields.addAll(getAdjacentFields(ownedField,candidates));
        }
        return adjacentFields;
    }

    /**
     * Suche alle Nachbarfelder in bezug auf gesamtes Spielbrett
     * @return ID-Liste der Nachbarfelder
     */
    public List<Integer> getAdjacentFields(List<Integer> ownedFields){
        List<Integer> adjacentFields = new ArrayList<>();
        for( TerrainField field : fields) {
            int id = field.getId();
            adjacentFields.addAll(getAdjacentFields(id,ownedFields));
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
            TerrainField field = fields[i];
            if(field.getOwner() != null && field.getOwner().equals(id)){fieldsByPlayer.add(i);}
        }
        return fieldsByPlayer;
    }


}
