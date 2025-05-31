package at.aau.serg.kingdombuilderserver.game;

public class GameBoardWinningConditions {
    public int[] getNeighbours(int id){
        return new int[] {1, 2, 3, 4};
    }

    public boolean getFieldType(int id) {
        return true;
    }
}
