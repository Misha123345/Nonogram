package sk.tuke.kpi.kp.nonogram.core;

public class Tile {
    private boolean isMarked;
    private final boolean isSolutionTile;

    public Tile(boolean isSolutionTile) {
        isMarked = false;
        this.isSolutionTile = isSolutionTile;
    }

    public boolean toggleMarked() {
        if (isSolutionTile) {
            isMarked = !isMarked;
            return true;
        }
        return false;
    }

    public boolean isSolutionTile() {
        return isSolutionTile;
    }

    public boolean isMarked() {
        return isMarked;
    }
}
