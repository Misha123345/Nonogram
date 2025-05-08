package sk.tuke.gamestudio.game.core.legend;

import sk.tuke.gamestudio.game.core.PuzzleField;

// Represents the left legend of a nonogram puzzle that shows row hints
public class LeftLegend extends Legend {
    
    // Calculates the left legend based on the solution state of the puzzle field
    @Override
    public void calculateLegend(PuzzleField field) {
        // Handle edge case of empty field
        if (field.getRows() == 0 || field.getColumns() == 0) {
            legend = new int[0][0];
            return;
        }
        
        // Temporary array to store all hints
        int[][] hintsPerRow = new int[field.getRows()][field.getColumns()];
        int[] hintsCount = new int[field.getRows()];
        
        // Count consecutive groups of filled cells for each row
        for (int row = 0; row < field.getRows(); row++) {
            int count = 0; // Counter for consecutive filled cells
            int hintIndex = 0; // Index for the current hint
            
            // Process row from left to right
            for (int col = 0; col < field.getColumns(); col++) {
                if (field.getTile(row, col).isSolutionTile()) {
                    // If cell is filled, increase counter
                    count++;
                } else if (count > 0) {
                    // If empty cell found after filled cells, record the group
                    hintsPerRow[row][hintIndex++] = count;
                    count = 0;
                }
            }
            
            // Check if there's an unprocessed group at the end of the row
            if (count > 0) {
                hintsPerRow[row][hintIndex++] = count;
            }
            
            // Save the number of hints for this row
            hintsCount[row] = hintIndex;
            
            // If no hints, add 0 as the only hint
            if (hintIndex == 0) {
                hintsPerRow[row][0] = 0;
                hintsCount[row] = 1;
            }
        }
        
        // Find the maximum number of hints across all rows
        int maxHints = 0;
        for (int count : hintsCount) {
            maxHints = Math.max(maxHints, count);
        }
        
        // Create the final hints array with exact dimensions
        legend = new int[field.getRows()][maxHints];
        
        // Copy hints from temporary array to final array with proper alignment
        for (int row = 0; row < field.getRows(); row++) {
            // Right-align hints (add zeros to the left if needed)
            for (int col = 0; col < maxHints; col++) {
                if (col < maxHints - hintsCount[row]) {
                    // Fill left side with zeros for alignment
                    legend[row][col] = 0;
                } else {
                    // Copy hints from temporary array
                    legend[row][col] = hintsPerRow[row][col - (maxHints - hintsCount[row])];
                }
            }
        }
    }
}
