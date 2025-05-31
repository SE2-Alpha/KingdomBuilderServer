package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.board.GameBoard;
import at.aau.serg.kingdombuilderserver.board.TerrainType;

import java.util.*;
import java.util.stream.Collectors;

public class WinningConditionEvaluator {

    private GameBoard board;
    private final List<Player> players;
    Map<Player, Integer> playerPoints = new HashMap<>();

    public WinningConditionEvaluator(GameBoard board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    /**
     * Geht in einer for-Schleife alle Spieler durch und berechnet für jeden die Gesamtpunktezahl.
     * @return winner(s)
     */
    public List<Player> evaluateWinner() {

        for (Player player : players) {
            int points =
                      evaluateHermits(player)
                    + evaluateMiners(player)
                    + evaluateDiscoverers(player)
                    + evaluateCastleFields(player);
            playerPoints.put(player, points);
        }

        int maxPoints = playerPoints.values().stream()
                .max(Integer::compareTo)
                .orElse(0);

        return playerPoints.entrySet().stream()
                .filter(entry -> entry.getValue() == maxPoints)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Berechnet die Punkte, die ein Spieler durch die Karte Einsiedler bekommt.
     * "1 Gold für jede einzelnstehende eigene Siedlung sowie für jedes separate Siedlungsgebiet."
     * @param player
     * @return points from hermits
     */
    public int evaluateHermits(Player player) {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> houses = new HashSet<>(player.getHouseFieldIds());
        int points = 0;

        for (int id : houses) {
            if (!visited.contains(id)) {
                Set<Integer> group = new HashSet<>();
                findConnectedGroup(id, houses, group, visited);
                points++; // eine neue zusammenhängende Gruppe -> 1 Punkt
            }
        }

        return points;
    }

    /**
     * Hilfsmethode für evaluateHermits() um zusammenhängende Siedlungen ausfindig zu machen.
     * @param id
     * @param houses
     * @param group
     * @param visited
     */
    private void findConnectedGroup(int id, Set<Integer> houses, Set<Integer> group, Set<Integer> visited) {
        if (!houses.contains(id) || visited.contains(id)) return;

        visited.add(id);
        group.add(id);

        /*  wieder "ein"kommentieren, sobald Methode getNeighbours() vorhanden sind
        for (int neighbor : getNeighbours(id)) {
            if (houses.contains(neighbor)) {
                findConnectedGroup(neighbor, houses, group, visited);
            }
        }
         */
    }

    /**
     * Berechnet die Punkte, die ein Spieler durch die Karte Bergleute bekommt.
     * "1 Gold für jede eigene Siedlung, die angrenzend an ein oder mehrere Bergfelder gebaut ist."
     * @param player
     * @return points from miners
     */
    public int evaluateMiners(Player player) {
        int points = 0;

        /* wieder "ein"kommentieren, sobald Methode getNeighbours() vorhanden sind
        for (int houseId : player.getHouseFieldIds()) {
            int[] neighbours = getNeighbours(houseId);

            for (int neighbourId : neighbours) {
                if (neighbourId >= 0 && neighbourId < 400 &&
                        board.getFieldType(neighbourId) == TerrainType.MOUNTAIN) {
                    points++;
                    break; // Nur 1 Punkt pro Siedlung, auch wenn mehrere Berge angrenzen
                }
            }
        }
         */

        return points;
    }

    /**
     * Berechnet die Punkte, die ein Spieler durch die Karte Entdecker bekommt.
     * "1 Gold für jede horizontale Linie, auf der mindestens eine eigene Siedlung gebaut ist."
     * @param player
     * @return points from Discoverer
     */
    public int evaluateDiscoverers(Player player){
        Set<Integer> rowsWithSettlements = new HashSet<>();

        for(int fieldId : player.getHouseFieldIds()){
            int row = fieldId / 20;
            rowsWithSettlements.add(row);
        }

        return rowsWithSettlements.size();
    }

    /**
     * Berechnet die Punkte, die ein Spieler durch die Burgfelder bekommt.
     * "Für jedes Burgfeld, an das mindestens eine eigene
     * Siedlung angrenzt, erhält der Spieler am Ende des Spiels
     * 3 Gold."
     * @param player
     * @return points from castle hex =CITY field
     */
    public int evaluateCastleFields(Player player) {
        int points = 0;

        /*  wieder "ein"kommentieren, sobald Methode getNeighbours() vorhanden sind
        for (int id = 0; id < 400; id++) {
            if (board.getFieldType(id) == TerrainType.CITY) {
                int[] neighbours = getNeighbours(id);

                for (int neighbourId : neighbours) {
                    // Sicherstellen, dass die Nachbar-ID gültig ist
                    if (neighbourId >= 0 && neighbourId < 400 &&
                            player.getHouseFieldIds().contains(neighbourId)) {
                        points += 3;
                        break; // Pro CITY-Feld nur einmal Punkte
                    }
                }
            }
        }

         */
        return points;
    }
}
