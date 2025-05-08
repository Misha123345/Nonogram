package sk.tuke.gamestudio.game.core;

/**
 * Manages player lives in the game
 */
public class Lives {
    private int livesCount;
    
    /**
     * Creates a new Lives with specified starting lives
     * @param startingLives Number of lives player starts with
     */
    public Lives(int startingLives) {
        // Ensure lives are never negative
        this.livesCount = Math.max(startingLives, 0);
    }
    
    /**
     * Decreases the number of lives by 1
     */
    public void decreaseLives() {
        if (livesCount > 0) {
            --livesCount;
        }
    }
    
    /**
     * Increases the number of lives by 1
     * @return The new number of lives
     */
    public int increaseLives() {
        return ++livesCount;
    }
    
    /**
     * Returns the current number of lives
     * @return Current number of lives
     */
    public int getLives() {
        return livesCount;
    }
    
    /**
     * Checks if player has any lives left
     * @return true if no lives remaining, false otherwise
     */
    public boolean hasNoLivesLeft() {
        return livesCount <= 0;
    }
} 