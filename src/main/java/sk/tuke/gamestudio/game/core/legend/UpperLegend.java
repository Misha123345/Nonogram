package sk.tuke.gamestudio.game.core.legend;

import sk.tuke.gamestudio.game.core.PuzzleField;

// Represents the upper legend of a nonogram puzzle that shows column hints
public class UpperLegend extends Legend {
    
    // Calculates the upper legend based on the solution state of the puzzle field
    @Override
    public void calculateLegend(PuzzleField field) {
        // Handle edge case of empty field
        if (field.getRows() == 0 || field.getColumns() == 0) {
            legend = new int[0][0];
            return;
        }
        
        // Temporary array to store all hints
        int[][] hintsPerColumn = new int[field.getColumns()][field.getRows()];
        int[] hintsCount = new int[field.getColumns()];
        
        // Count consecutive groups of filled cells for each column
        for (int col = 0; col < field.getColumns(); col++) {
            int count = 0; // Counter for consecutive filled cells
            int hintIndex = 0; // Index for the current hint
            
            // Process column from top to bottom
            for (int row = 0; row < field.getRows(); row++) {
                if (field.getTile(row, col).isSolutionTile()) {
                    // If cell is filled, increase counter
                    count++;
                } else if (count > 0) {
                    // If empty cell found after filled cells, record the group
                    hintsPerColumn[col][hintIndex++] = count;
                    count = 0;
                }
            }
            
            // Check if there's an unprocessed group at the end of the column
            if (count > 0) {
                hintsPerColumn[col][hintIndex++] = count;
            }
            
            // Save the number of hints for this column
            hintsCount[col] = hintIndex;
            
            // If no hints, add 0 as the only hint
            if (hintIndex == 0) {
                hintsPerColumn[col][0] = 0;
                hintsCount[col] = 1;
            }
        }
        
        // Find the maximum number of hints across all columns
        int maxHints = 0;
        for (int count : hintsCount) {
            maxHints = Math.max(maxHints, count);
        }
        
        // Create the final hints array with exact dimensions
        legend = new int[field.getColumns()][maxHints];
        
        // Copy hints from temporary array to final array with proper alignment
        for (int col = 0; col < field.getColumns(); col++) {
            int destRow = maxHints - 1; // Start from the bottom
            
            // Fill in the hints from bottom to top
            for (int hintIdx = hintsCount[col] - 1; hintIdx >= 0; hintIdx--) {
                legend[col][destRow--] = hintsPerColumn[col][hintIdx];
            }
            
            // Fill remaining top positions with zeros
            while (destRow >= 0) {
                legend[col][destRow--] = 0;
            }
        }
    }
}
