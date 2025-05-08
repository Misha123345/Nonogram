package sk.tuke.gamestudio.game.core;

import lombok.Getter;
import lombok.Setter;
import sk.tuke.gamestudio.game.core.legend.LeftLegend;
import sk.tuke.gamestudio.game.core.legend.UpperLegend;
import java.util.Random;

public class PuzzleField {
    private final Tile[][] field;
    @Getter
    private final int rows;
    @Getter
    private final int columns;
    @Getter
    private final UpperLegend upperLegend;
    @Getter
    private final LeftLegend leftLegend;
    @Setter
    @Getter
    private FieldState fieldState;
    @Getter
    private final long startTime;
    private int incorrectMoves;
    @Getter
    private final Lives lives;

    // Initializes the puzzle field from a boolean pattern
    public PuzzleField(boolean[][] image) {
        this(image, 3);
    }
    
    // Initializes the puzzle field from a boolean pattern with specified lives
    public PuzzleField(boolean[][] image, int livesCount) {
        this(image, livesCount, System.currentTimeMillis());
    }

    // Initializes the puzzle field with custom start time (for saved games)
    public PuzzleField(boolean[][] image, int livesCount, long startTime) {
        this.rows = image.length;
        this.columns = image.length > 0 ? image[0].length : 0;
        field = new Tile[rows][columns];
        fieldState = FieldState.PLAYING;
        this.startTime = startTime;
        incorrectMoves = 0;
        lives = new Lives(livesCount);

        // Initialize tiles
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                field[i][j] = new Tile(image[i][j]);
            }
        }

        // Initialize legends
        upperLegend = new UpperLegend();
        upperLegend.calculateLegend(this);
        leftLegend = new LeftLegend();
        leftLegend.calculateLegend(this);
        
        // Auto-solve empty fields
        if (isAllEmpty()) {
            fieldState = FieldState.SOLVED;
        }
    }

    // Returns the tile at the specified position
    public Tile getTile(int x, int y) {
        return field[x][y];
    }

    // Marks a tile at the specified position and checks if puzzle is solved
    public boolean markTile(int row, int column) {
        if (row < rows && row >= 0 && column < columns && column >= 0) {
            boolean result = field[row][column].toggleMarked();
            
            if (!result) {
                incorrectMoves++;
                lives.decreaseLives();
                
                if (lives.hasNoLivesLeft()) {
                    this.fieldState = FieldState.FAILED;
                }
            }
            
            if (isSolved()) {
                this.fieldState = FieldState.SOLVED;
            }
            
            return result;
        }
        return false;
    }

    // Checks if the puzzle is solved
    private boolean isSolved() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (field[row][col].isSolutionTile() && !field[row][col].isMarked()) {
                    return false;
                }
                if (!field[row][col].isSolutionTile() && field[row][col].isMarked()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // Checks if all cells in the puzzle are empty
    private boolean isAllEmpty() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (field[row][col].isSolutionTile()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // Calculates the player's score based on completion time and accuracy
    public int getScore() {
        if (fieldState != FieldState.SOLVED || isAllEmpty()) {
            return 0;
        }
        
        // Calculate time penalty
        long endTime = System.currentTimeMillis();
        int timeInSeconds = (int) ((endTime - startTime) / 1000);
        int baseScore = rows * columns * 10;
        int timeDeduction = Math.min(baseScore * 3/4, timeInSeconds * 2);
        
        // Calculate mistake penalty
        int incorrectMovesDeduction = incorrectMoves * 20;
        
        return Math.max(1, baseScore - timeDeduction - incorrectMovesDeduction);
    }

    /**
     * Creates a new PuzzleField with random symmetric pattern
     * @param size Size of the field (size x size)
     * @return New PuzzleField instance with random pattern
     */
    public static PuzzleField createRandomField(int size) {
        return createRandomField(size, 3);
    }
    
    /**
     * Creates a new PuzzleField with random symmetric pattern and specified lives
     * @param size Size of the field (size x size)
     * @param livesCount Number of lives
     * @return New PuzzleField instance with random pattern
     */
    public static PuzzleField createRandomField(int size, int livesCount) {
        return new PuzzleField(generateRandomSymmetricPattern(size), livesCount);
    }
    
    /**
     * Generates a random symmetric pattern
     * @param size Size of the pattern
     * @return 2D boolean array representing the pattern
     */
    private static boolean[][] generateRandomSymmetricPattern(int size) {
        boolean[][] pattern = new boolean[size][size];
        Random random = new Random();
        
        // Fill left half randomly
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size / 2; col++) {
                pattern[row][col] = random.nextDouble() > 0.6; // 40% chance of cell being filled
            }
        }
        
        // Mirror to right half
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size / 2; col++) {
                pattern[row][size - 1 - col] = pattern[row][col];
            }
        }
        
        // If size is odd, fill middle column with 50/50 chance
        if (size % 2 == 1) {
            int middleCol = size / 2;
            for (int row = 0; row < size; row++) {
                pattern[row][middleCol] = random.nextDouble() > 0.5;
            }
        }
        
        return pattern;
    }
}
