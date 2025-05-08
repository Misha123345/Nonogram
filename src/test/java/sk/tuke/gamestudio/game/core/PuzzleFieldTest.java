package sk.tuke.gamestudio.game.core;

import org.junit.jupiter.api.Test;

import sk.tuke.gamestudio.game.core.FieldState;
import sk.tuke.gamestudio.game.core.PuzzleField;

import static org.junit.jupiter.api.Assertions.*;

public class PuzzleFieldTest {

    private static final boolean[][] TEST_PATTERN = {
            {true, false, true},
            {false, true, false},
            {true, false, true}
    };
    
    @Test
    public void testNewPuzzleField() {
        PuzzleField field = new PuzzleField(TEST_PATTERN);
        
        assertEquals(3, field.getRows());
        assertEquals(3, field.getColumns());
        assertEquals(FieldState.PLAYING, field.getFieldState());
        
        // Check initial state of tiles
        for (int row = 0; row < field.getRows(); row++) {
            for (int col = 0; col < field.getColumns(); col++) {
                assertFalse(field.getTile(row, col).isMarked());
                assertEquals(TEST_PATTERN[row][col], field.getTile(row, col).isSolutionTile());
            }
        }
    }
    
    @Test
    public void testMarkCorrectTile() {
        PuzzleField field = new PuzzleField(TEST_PATTERN);
        
        // Mark a correct tile
        assertTrue(field.markTile(0, 0)); // Top left corner - true
        assertTrue(field.getTile(0, 0).isMarked());
        assertEquals(FieldState.PLAYING, field.getFieldState());
        
        // Repeated marking should not change state
        assertTrue(field.markTile(0, 0));
    }
    
    @Test
    public void testMarkWrongTile() {
        PuzzleField field = new PuzzleField(TEST_PATTERN);
        
        // Mark an incorrect tile
        assertFalse(field.markTile(0, 1)); // Top middle - false
        assertFalse(field.getTile(0, 1).isMarked());
        assertEquals(FieldState.PLAYING, field.getFieldState());
    }
    
    @Test
    public void testSolveField() {
        PuzzleField field = new PuzzleField(TEST_PATTERN);
        
        // Mark all correct tiles
        field.markTile(0, 0); // top left
        field.markTile(0, 2); // top right
        field.markTile(1, 1); // middle
        field.markTile(2, 0); // bottom left
        field.markTile(2, 2); // bottom right
        
        // Check that all are marked correctly
        assertTrue(field.getTile(0, 0).isMarked());
        assertTrue(field.getTile(0, 2).isMarked());
        assertTrue(field.getTile(1, 1).isMarked());
        assertTrue(field.getTile(2, 0).isMarked());
        assertTrue(field.getTile(2, 2).isMarked());
        
        // Field should be marked as solved
        assertEquals(FieldState.SOLVED, field.getFieldState());
        
        // Check score calculation - score will vary based on time, but it will be at least 1
        assertTrue(field.getScore() >= 1);
    }
    
    @Test
    public void testGetLegends() {
        PuzzleField field = new PuzzleField(TEST_PATTERN);
        
        // Check upper legends
        int[][] upperLegend = field.getUpperLegend().getLegend();
        
        // Verify dimensions
        assertEquals(3, upperLegend.length); // 3 columns
        assertEquals(2, upperLegend[0].length); // Each column has 2 values (max hint count)

        // Check left legends
        int[][] leftLegend = field.getLeftLegend().getLegend();
        
        // Verify dimensions
        assertEquals(3, leftLegend.length); // 3 rows
        assertEquals(2, leftLegend[0].length); // Each row has 2 values (max hint count)
        
        // Adapt assertions to match the actual implementation
        // For column 1 (index 0) - two separate groups of size 1
        assertEquals(1, upperLegend[0][0]);
        assertEquals(1, upperLegend[0][1]);
        
        // For column 2 (index 1) - one group of size 1
        assertEquals(0, upperLegend[1][0]);
        assertEquals(1, upperLegend[1][1]);
        
        // For column 3 (index 2) - two separate groups of size 1
        assertEquals(1, upperLegend[2][0]);
        assertEquals(1, upperLegend[2][1]);
        
        // For row 1 (index 0) - two separate groups of size 1
        assertEquals(1, leftLegend[0][0]);
        assertEquals(1, leftLegend[0][1]);
        
        // For row 2 (index 1) - one group of size 1
        assertEquals(0, leftLegend[1][0]);
        assertEquals(1, leftLegend[1][1]);
        
        // For row 3 (index 2) - two separate groups of size 1
        assertEquals(1, leftLegend[2][0]);
        assertEquals(1, leftLegend[2][1]);
    }
    
    @Test
    public void testEmptyField() {
        boolean[][] emptyPattern = {
            {false, false},
            {false, false}
        };
        
        PuzzleField field = new PuzzleField(emptyPattern);
        
        // Field should be marked as solved, as there are no tiles to mark
        assertEquals(FieldState.SOLVED, field.getFieldState());
        assertEquals(0, field.getScore());
    }
} 