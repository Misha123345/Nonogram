package sk.tuke.gamestudio.game.core;

import org.junit.jupiter.api.Test;

import sk.tuke.gamestudio.game.core.Lives;

import static org.junit.jupiter.api.Assertions.*;

public class LivesTest {

    @Test
    public void testNewLives() {
        Lives lives = new Lives(3);
        assertEquals(3, lives.getLives());
        assertFalse(lives.hasNoLivesLeft());
    }
    
    @Test
    public void testDecreaseLives() {
        Lives lives = new Lives(3);
        
        lives.decreaseLives();
        assertEquals(2, lives.getLives());
        assertFalse(lives.hasNoLivesLeft());
        
        lives.decreaseLives();
        assertEquals(1, lives.getLives());
        assertFalse(lives.hasNoLivesLeft());
        
        lives.decreaseLives();
        assertEquals(0, lives.getLives());
        assertTrue(lives.hasNoLivesLeft());
        
        // Lives should not go below zero
        lives.decreaseLives();
        assertEquals(0, lives.getLives());
        assertTrue(lives.hasNoLivesLeft());
    }
    
    @Test
    public void testZeroInitialLives() {
        Lives lives = new Lives(0);
        assertEquals(0, lives.getLives());
        assertTrue(lives.hasNoLivesLeft());
    }
    
    @Test
    public void testNegativeInitialLives() {
        // Negative initial lives should be set to 0
        Lives lives = new Lives(-5);
        assertEquals(0, lives.getLives());
        assertTrue(lives.hasNoLivesLeft());
    }
} 