package main.java.sk.tuke.kpi.kp.nonogram.core.legend;

import sk.tuke.kpi.kp.nonogram.PuzzleField;

public class UpperLegend extends Legend {

    @Override
    public void calculateLegend(PuzzleField field) {
        legend = new int[field.getRows()][field.getColumns()];

        for (int row = 0; row < field.getRows(); row++) {
            int currentLength = 0;
            int colIndex = 0;

            for (int col = 0; col < field.getColumns(); col++) {
                if (field.getTile(row, col).isSolutionTile()) {
                    currentLength++;
                } else {
                    if (currentLength > 0) {
                        legend[row][colIndex] = currentLength;
                        colIndex++;
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
