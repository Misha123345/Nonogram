package sk.tuke.gamestudio.game.core;

public class Tile {
    private boolean isMarked;
    private final boolean isSolutionTile;

    public Tile(boolean isSolutionTile) {
        isMarked = false;
        this.isSolutionTile = isSolutionTile;
    }

    // Attempts to mark the tile; returns true if successful (correct marking)
    public boolean toggleMarked() {
        if (isSolutionTile) {
            isMarked = true;
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
