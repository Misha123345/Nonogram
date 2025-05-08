package main.java.sk.tuke.kpi.kp.nonogram.core.legend;

import sk.tuke.kpi.kp.nonogram.PuzzleField;

public class LeftLegend extends Legend {
    @Override
    public void calculateLegend(PuzzleField field) {
        legend = new int[field.getRows()][field.getColumns()];

        for (int row = 0; row < field.getRows(); row++) {
            int currentLength = 0;
            int colIndex = field.getColumns() - 1;

            for (int column = field.getColumns() - 1; column >= 0; column--) {
                if (field.getTile(row, column).isSolutionTile()) {
                    currentLength++;
                } else {
                    if (currentLength > 0) {
                        legend[row][colIndex] = currentLength;
                        colIndex--;
                    }
                    currentLength = 0;
                }
            }

            if (currentLength > 0) {
                legend[row][colIndex] = currentLength;
            }
        }
    }
}
