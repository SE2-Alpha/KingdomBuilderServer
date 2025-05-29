package at.aau.serg.kingdombuilderserver.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        return 0;
    }

}
