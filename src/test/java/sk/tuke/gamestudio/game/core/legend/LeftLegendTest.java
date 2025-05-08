package sk.tuke.gamestudio.game.core.legend;

import org.junit.jupiter.api.Test;

import sk.tuke.gamestudio.game.core.PuzzleField;

import static org.junit.jupiter.api.Assertions.*;

public class LeftLegendTest {

    @Test
    public void testEmptyPattern() {
        boolean[][] pattern = {
            {false, false, false},
            {false, false, false}
        };
        
        PuzzleField field = new PuzzleField(pattern);
        int[][] legend = field.getLeftLegend().getLegend();
        
        // Check dimensions
        assertEquals(2, legend.length); // 2 rows
        assertEquals(1, legend[0].length); // 1 hint (maximum)
        
        // Check content (empty hints)
        assertEquals(0, legend[0][0]); // First row
        assertEquals(0, legend[1][0]); // Second row
    }
    
    @Test
    public void testSimplePattern() {
        boolean[][] pattern = {
            {true, false, true},  // 1 1
            {true, true, true},   // 3
            {false, false, false} // 0
        };
        
        PuzzleField field = new PuzzleField(pattern);
        int[][] legend = field.getLeftLegend().getLegend();
        
        // Check dimensions
        assertEquals(3, legend.length); // 3 rows
        assertEquals(2, legend[0].length); // 2 hints (maximum)
        
        // Check first row (1 1)
        assertEquals(1, legend[0][0]);
        assertEquals(1, legend[0][1]);
        
        // Check second row (3, right-aligned: 0 3)
        assertEquals(0, legend[1][0]);
        assertEquals(3, legend[1][1]);
        
        // Check third row (empty: 0 0)
        assertEquals(0, legend[2][0]);
        assertEquals(0, legend[2][1]);
    }
    
    @Test
    public void testComplexPattern() {
        boolean[][] pattern = {
            {true, true, false, false, true, true, true}, // 2 3
            {true, false, true, false, true, false, true}, // 1 1 1 1
            {true, true, true, true, true, true, true},   // 7
            {false, false, false, false, false, false, false} // 0
        };
        
        PuzzleField field = new PuzzleField(pattern);
        int[][] legend = field.getLeftLegend().getLegend();
        
        // Check dimensions
        assertEquals(4, legend.length); // 4 rows
        assertEquals(4, legend[0].length); // 4 hints (maximum)
        
        // Check first row (2 3, right-aligned: 0 0 2 3)
        assertEquals(0, legend[0][0]);
        assertEquals(0, legend[0][1]);
        assertEquals(2, legend[0][2]);
        assertEquals(3, legend[0][3]);
        
        // Check second row (1 1 1 1)
        assertEquals(1, legend[1][0]);
        assertEquals(1, legend[1][1]);
        assertEquals(1, legend[1][2]);
        assertEquals(1, legend[1][3]);
        
        // Check third row (7, right-aligned: 0 0 0 7)
        assertEquals(0, legend[2][0]);
        assertEquals(0, legend[2][1]);
        assertEquals(0, legend[2][2]);
        assertEquals(7, legend[2][3]);
        
        // Check fourth row (empty: 0 0 0 0)
        assertEquals(0, legend[3][0]);
        assertEquals(0, legend[3][1]);
        assertEquals(0, legend[3][2]);
        assertEquals(0, legend[3][3]);
    }
} 