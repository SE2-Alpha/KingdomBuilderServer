package at.aau.serg.kingdombuilderserver.game;

public class GameHousePosition {

    private final int x;
    private final int y;

    /**
     * Konstruktor für GameHousePosition.
     * Erstellt eine neue Instanz mit einer x- und y-Position.
     *
     * @param x Die x-Position des Hauses.
     * @param y Die y-Position des Hauses.
     */
    public GameHousePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "GameHousePosition{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
