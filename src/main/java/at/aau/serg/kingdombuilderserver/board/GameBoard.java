package at.aau.serg.kingdombuilderserver.board;

import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantFields;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantOasis;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantTavern;
import at.aau.serg.kingdombuilderserver.board.quadrants.QuadrantTower;
import at.aau.serg.kingdombuilderserver.game.GameHousePosition;
import at.aau.serg.kingdombuilderserver.game.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.*;
import java.util.function.Supplier;

public class GameBoard {
    private static final int SIZE = 400; // 20x20
    @Getter
    private final TerrainField[] fields = new TerrainField[SIZE];

    @JsonIgnore
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
    -Special House placing (Farm, Oracle, Tavern, Tower): Separate function?
            - Farm: Build one additional settlement on Grass (adjacent if possible)
            - Oracle: Build one additional settlement of the same type as required Field type (Draw Card, must be adjacent)
            - Tavern: Build one additional settlement on the end of a straight line of at least 3 settlements
            - Tower: Build one Settlement on the Edge of the Board (adjacent if possible)
     */

    //TODO Special action implementation

    /**
     * Check if house can be placed given parameters and places if possible
     * @param activePlayer currently active player (by GameManager)
     * @param activeList List of IDs of houses placed this round by active player
     * @param position clicked position on wich house will be built if possible
     * @param round current round number (by GameManager)
     */
    public void placeHouse(Player activePlayer,List<Integer> activeList, GameHousePosition position, int round) {
        if (activePlayer == null || position == null || activePlayer.getCurrentCard() == null || activeList == null) {
            int errcode = 0;
            if(activeList == null) errcode +=1;
            if(activePlayer == null) errcode +=10; else if(activePlayer.getCurrentCard() == null) errcode +=100;
            if(position == null) errcode +=1000;

            throw new IllegalArgumentException("Aktiver Spieler, Position und Karte dürfen nicht null sein. " + errcode);
        }

        if(activePlayer.getRemainingSettlements() == 0){
            throw new IllegalArgumentException("Spieler hat keine Gebäude übrig " + activePlayer);
        }

        if(activeList.size() >= 3){
            throw new IllegalArgumentException("Spieler hat schon 3 Gebäude Platziert " + activePlayer);
        }

        if(!isPositionValid(position)){
            throw new IllegalArgumentException("Kann hier nichts platzieren " + position);
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
                removeLegally(field,activeList,activePlayer);
                return;
            }else{
                throw new IllegalStateException("Feld ist bereits von einem anderen Spieler besetzt: " + field);
            }
        }

        List<Integer> freeFieldsOfCurrentType = getFreeFieldsOfType(currentCard);
        List<Integer> builtByActivePlayer = getFieldsBuiltBy(currentPID);
        List<Integer> allFreeAdjacentFields = getAdjacentFields(builtByActivePlayer);
        List<Integer> freeAdjacentCurrentTypeFields = getIntersection(allFreeAdjacentFields, freeFieldsOfCurrentType);


//        Info dump
//        System.out.println("My Field-ID: "+ id);
//        System.out.println("freeFieldsOfCurrentType: "+freeFieldsOfCurrentType);
//        System.out.println("builtByActivePlayer: "+builtByActivePlayer);
//        System.out.println("allFreeAdjacentFields: "+allFreeAdjacentFields);
//        System.out.println("freeAdjacentCurrentTypeFields: "+freeAdjacentCurrentTypeFields);

        //First building
        if(builtByActivePlayer.isEmpty() && freeFieldsOfCurrentType.contains(id)
        ){
            placeLegally(field,activePlayer,round,activeList);
            System.out.println("Built field "+field.getId());
            return;
        }
        if(freeFieldsOfCurrentType.isEmpty()){//When all fields of current Type are occupied (unlikely) allow player to place on any neighboring field
            if(allFreeAdjacentFields.contains(id)){
                placeLegally(field,activePlayer,round,activeList);
                System.out.println("Built field "+field.getId());
                return;
            }else{
                throw new IllegalStateException("Gezogener FeldTyp ist voll und feld grenzt nicht an bebautes feld" + allFreeAdjacentFields);
            }

        }

        if(
                freeAdjacentCurrentTypeFields.contains(id) ||
                freeAdjacentCurrentTypeFields.isEmpty() &&
                (freeFieldsOfCurrentType.contains(id))
        ){
            placeLegally(field,activePlayer,round,activeList);
            System.out.println("Built field "+field.getId());
            return;
        }

        if(field.getType() != currentCard) {
            throw new IllegalStateException("Feld hat falschen FeldTypen: " + field.getType() + " statt "+ currentCard);
        }


        if(!freeAdjacentCurrentTypeFields.isEmpty()  && !freeAdjacentCurrentTypeFields.contains(id)
                || !allFreeAdjacentFields.isEmpty() && !allFreeAdjacentFields.contains(id)
        ){
            throw new IllegalStateException("Feld an nicht erlaubter Position: " + field.getId() + " "
                    +freeAdjacentCurrentTypeFields + " " + allFreeAdjacentFields );
        }

        //Failsafe
        throw new IllegalStateException("Feld konnte nicht bebaut werden: " + field);
    }

    /**
     * Place building, set owner and round in field, add field-ID to current list (by GameManager)
     * @param field position of new house
     * @param player active Player (by GameManager)
     * @param round int round number
     * @param buffer list of houses built in current round
     */
    public void placeLegally(TerrainField field, Player player, int round,List<Integer> buffer){
        field.setOwner(player.getId());
        field.setOwnerSinceRound(round);
        buffer.add(field.getId());
        player.decreaseSettlementsBy(1);
    }

    /**
     * Removes house from selected field by clearing all relevant field + all buildings placed after it
     * @param field field to be cleared
     * @param buffer list of houses built in current round
     */
    public void removeLegally(TerrainField field,List<Integer> buffer, Player player){
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
        player.increaseSettlementsBy(buffer.size());

    }

    public TerrainType getFieldType(int id) {
        if (id < 0 || id >= fields.length) {
            throw new IllegalArgumentException("Ungültige Feld-ID: " + id);
        }
        return fields[id].getType();
    }

    public void undoMove(List<Integer> housesToUndo, Player player){
        int numberOfHouses = housesToUndo.size();
        for (Integer fieldId: housesToUndo) {
            TerrainField field = fields[fieldId];
            if (field.getOwner() != null && field.getOwner().equals(player.getId())){
                field.setOwner(null);
                field.setOwnerSinceRound(-1);
            }
        }
        player.increaseSettlementsBy(numberOfHouses);
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
        Set<Integer> adjacentFields = new HashSet<>();
        int[] neighbours = TerrainField.getNeighbours(center);
        for(int neighbour : neighbours) {
            if(candidates.contains(neighbour)) {adjacentFields.add(neighbour);}
        }
        return adjacentFields.stream().toList();
    }

    /**
     * Suche alle Nachbarfelder in bezug auf gesamtes Spielbrett
     * @return ID-Liste der Nachbarfelder
     */
    public List<Integer> getAdjacentFields(List<Integer> ownedFields){
        List<Integer> neighbours = TerrainField.getNeighbours(ownedFields);
        Set<Integer> adjacentFields = new HashSet<>();
        for(int field : neighbours) {
            if(!ownedFields.contains(field)) {adjacentFields.add(field);}
        }
        return adjacentFields.stream().toList();
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
        Set<Integer> freeFields = new HashSet<>();
        for(TerrainField f: fields){
            if(f.getType() == type && f.getOwner() == null){freeFields.add(f.getId());}
        }
        return freeFields.stream().toList();
    }

    /**
     * @param id Spieler ID
     * @return Liste der Felder die von Spieler mit id bebaut wurden
     */
    public List<Integer> getFieldsBuiltBy(String id){
        Set<Integer> fieldsByPlayer = new HashSet<>();
        for(TerrainField f: fields){
            if(f.getOwner() != null && f.getOwner().equals(id)){fieldsByPlayer.add(f.getId());}
        }
        return fieldsByPlayer.stream().toList();
    }

    public List<Integer> getIntersection(List<Integer> listA, List<Integer> listB){
        Set<Integer> intersection = new HashSet<>();
        for(int i: listA){
            if(listB.contains(i)){intersection.add(i);}
        }
        return intersection.stream().toList();
    }
}
