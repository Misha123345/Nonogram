package main.java.sk.tuke.kpi.kp.nonogram.core;

import lombok.Getter;
import sk.tuke.kpi.kp.nonogram.legend.LeftLegend;
import sk.tuke.kpi.kp.nonogram.legend.UpperLegend;

public class PuzzleField {
    private final Tile[][] field;
    @Getter
    private final int rows;
    @Getter
    private final int columns;
    @Getter
    private final UpperLegend upperLegend;
    @Getter
    private final LeftLegend leftLegend;
    @Getter
    private FieldState fieldState;

    public PuzzleField(int rows, int columns, boolean[][] image) {
        this.rows = rows;
        this.columns = columns;
        field = new Tile[rows][columns];
        fieldState = FieldState.PLAYING;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                field[i][j] = new Tile(image[i][j]);
            }
        }

        upperLegend = new UpperLegend();
        upperLegend.calculateLegend(this);
        leftLegend = new LeftLegend();
        leftLegend.calculateLegend(this);
    }

    public Tile getTile(int x, int y) {
        return field[x][y];
    }

    public void markTile(int row, int column) {
        if (row < rows && row >= 0 && column < columns && column >= 0) {
            field[row][column].toggleMarked();
            if (isSolved()) {
                this.fieldState = FieldState.SOLVED;
            };
        }
    }

    private boolean isSolved() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (!field[row][col].isMarked() && field[row][col].isSolutionTile()) return false;
                if (field[row][col].isMarked() && !field[row][col].isSolutionTile()) return false;
            }
        }
        return true;
    }
}
