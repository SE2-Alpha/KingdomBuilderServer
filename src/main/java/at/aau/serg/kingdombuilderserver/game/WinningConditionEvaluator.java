package at.aau.serg.kingdombuilderserver.game;

import java.util.*;

public class WinningConditionEvaluator {
   //Platzhalterklasse soll durch echtes Gameboard ersetzt werden
    private GameBoardWinningConditions board;
    private final List<Player> players;
    Map<Player, Integer> playerPoints = new HashMap<>();

    public WinningConditionEvaluator(GameBoardWinningConditions board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public Player evaluateWinner() {

        for (Player player : players) {

        }

        return players.get(0);
    }

    //Einsiedler
    public int evaluateHermits(Player player) {

        return 0;
    }

    //Bergleute
    public int evaluateMiners(Player player) {

        return 0;
    }

    //Entdecker
    public int evaluateDiscoverers(Player player){
        Set<Integer> rowsWithSettlements = new HashSet<>();

        for(int fieldId : player.getHouseFieldIds()){
            int row = fieldId / 20;
            rowsWithSettlements.add(row);
        }

        return rowsWithSettlements.size();
    }

}
