package sk.tuke.gamestudio.game.core.legend;

import org.junit.jupiter.api.Test;

import sk.tuke.gamestudio.game.core.PuzzleField;

import static org.junit.jupiter.api.Assertions.*;

public class UpperLegendTest {

    @Test
    public void testEmptyPattern() {
        boolean[][] pattern = {
            {false, false, false},
            {false, false, false}
        };
        
        PuzzleField field = new PuzzleField(pattern);
        int[][] legend = field.getUpperLegend().getLegend();
        
        // Check dimensions
        assertEquals(3, legend.length); // 3 columns
        assertEquals(1, legend[0].length); // 1 hint (maximum)
        
        // Check content (empty hints)
        assertEquals(0, legend[0][0]); // First column
        assertEquals(0, legend[1][0]); // Second column
        assertEquals(0, legend[2][0]); // Third column
    }
    
    @Test
    public void testSimplePattern() {
        boolean[][] pattern = {
            {true, false, true},
            {true, true, false},
            {false, true, true}
        };
        
        PuzzleField field = new PuzzleField(pattern);
        int[][] legend = field.getUpperLegend().getLegend();
        
        // Check dimensions
        assertEquals(3, legend.length); // 3 columns
        assertEquals(2, legend[0].length); // 2 hints (maximum)
        
        // Check first column (2, bottom-aligned: 0 2)
        assertEquals(0, legend[0][0]);
        assertEquals(2, legend[0][1]);
        
        // Check second column (2, bottom-aligned: 0 2)
        assertEquals(0, legend[1][0]);
        assertEquals(2, legend[1][1]);
        
        // Check third column (1 1)
        assertEquals(1, legend[2][0]);
        assertEquals(1, legend[2][1]);
    }
    
    @Test
    public void testComplexPattern() {
        boolean[][] pattern = {
            {true, false, true, false},
            {true, true, true, false},
            {false, true, false, true},
            {false, true, false, true}
        };
        
        PuzzleField field = new PuzzleField(pattern);
        int[][] legend = field.getUpperLegend().getLegend();
        
        // Check dimensions
        assertEquals(4, legend.length); // 4 columns
        assertEquals(1, legend[0].length); // 1 hint (maximum)
        
        // Check first column (2)
        assertEquals(2, legend[0][0]);
        
        // Check second column (3)
        assertEquals(3, legend[1][0]);
        
        // Check third column (2)
        assertEquals(2, legend[2][0]);
        
        // Check fourth column (2)
        assertEquals(2, legend[3][0]);
    }
} 