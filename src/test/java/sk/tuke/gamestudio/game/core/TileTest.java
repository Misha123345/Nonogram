package sk.tuke.gamestudio.game.core;

import org.junit.jupiter.api.Test;

import sk.tuke.gamestudio.game.core.Tile;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    @Test
    public void testNewTile() {
        Tile tile = new Tile(false);
        assertFalse(tile.isMarked());
        assertFalse(tile.isSolutionTile());
        
        Tile solutionTile = new Tile(true);
        assertFalse(solutionTile.isMarked());
        assertTrue(solutionTile.isSolutionTile());
    }
    
    @Test
    public void testMarkTile() {
        // Normal tile
        Tile tile = new Tile(false);
        assertFalse(tile.toggleMarked());
        assertFalse(tile.isMarked());
        
        // Repeated marking should not change anything
        assertFalse(tile.toggleMarked());
        assertFalse(tile.isMarked());
    }
    
    @Test
    public void testMarkSolutionTile() {
        // Solution tile
        Tile solutionTile = new Tile(true);
        assertTrue(solutionTile.toggleMarked());
        assertTrue(solutionTile.isMarked());
    }
} 