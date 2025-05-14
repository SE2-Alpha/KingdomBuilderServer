package at.aau.serg.kingdombuilderserver.game;

import java.awt.*;
//Colors are Set when Room is started
//Colors are assigned through List-iteration in Room
public final class PlayerColor {

    private PlayerColor() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Player 1
     */
    public static final int RED = new Color(255, 0, 0).darker().getRGB();

    /**
     * Player2
     */
    public static final int BLUE = new Color(132, 43, 226).darker().getRGB();

    /**
     * Player 3
     */
    public static final int WHITE = new Color(255, 255, 255).darker().getRGB();

    /**
     * Player 4
     */
    public static final int BLACK = new Color(0, 0, 0).darker().getRGB();

    public static int getColor(int index){
        return switch (index) {
            case 0 -> RED;
            case 1 -> BLUE;
            case 2 -> WHITE;
            case 3 -> BLACK;
            default -> -1;
        };
    }
}
