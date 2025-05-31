package at.aau.serg.kingdombuilderserver.board;

public class QuadrantUtils {
    // Rotiert das Feld-Array eines Quadranten um 90° Schritte (1=90°, 2=180°, 3=270°)
    public static TerrainType[] rotateQuadrant(TerrainType[] original, int rotation) {
        TerrainType[] rotated = new TerrainType[100];
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                int oldIdx = r * 10 + c;
                int newIdx;
                switch (rotation) {
                    case 1: // 90°
                        newIdx = c * 10 + (9 - r);
                        break;
                    case 2: // 180°
                        newIdx = (9 - r) * 10 + (9 - c);
                        break;
                    case 3: // 270°
                        newIdx = (9 - c) * 10 + r;
                        break;
                    default: // 0°
                        newIdx = oldIdx;
                }
                rotated[newIdx] = original[oldIdx];
            }
        }
        return rotated;
    }
}
