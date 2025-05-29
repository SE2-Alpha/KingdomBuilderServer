package at.aau.serg.kingdombuilderserver.game;

import java.util.*;
import java.util.stream.Collectors;

public class WinningConditionEvaluator {
   //Platzhalterklasse soll durch echtes Gameboard ersetzt werden
    private GameBoardWinningConditions board;
    private final List<Player> players;
    Map<Player, Integer> playerPoints = new HashMap<>();

    public WinningConditionEvaluator(GameBoardWinningConditions board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    /**
     * Geht in einer for-Schleife alle Spieler durch und berechnet für jeden die Gesamtpunktezahl.
     *
     * @return winner(s)
     */
    public List<Player> evaluateWinner() {

        for (Player player : players) {
            int points =
                      evaluateHermits(player)
                    + evaluateMiners(player)
                    + evaluateDiscoverers(player);
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

        return 0;
    }

    /**
     * Berechnet die Punkte, die ein Spieler durch die Karte Bergleute bekommt.
     * "1 Gold für jede eigene Siedlung, die angrenzend an ein oder mehrere Bergfelder gebaut ist."
     * @param player
     * @return points from miners
     */
    public int evaluateMiners(Player player) {

        return 0;
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

}
