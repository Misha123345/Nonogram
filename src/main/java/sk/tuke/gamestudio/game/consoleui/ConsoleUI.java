package sk.tuke.gamestudio.game.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.core.FieldState;
import sk.tuke.gamestudio.game.core.Lives;
import sk.tuke.gamestudio.game.core.PuzzleField;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.ScoreException;
import sk.tuke.gamestudio.service.ScoreService;
import sk.tuke.gamestudio.service.CommentException;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingException;
import sk.tuke.gamestudio.service.RatingService;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Console user interface for the Nonogram game. Handles rendering and user input
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private PuzzleField field;
    private static final Pattern INPUT_PATTERN = Pattern.compile("([a-zA-Z])([1-9]\\d?)");
    private String errorMessage = null;
    private int[][] upperLegend;
    private int[][] leftLegend;
    private int maxLeftLegendWidth;
    private Lives lives;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;
    private int finalScore; // Store final score when game is completed
    
    // Difficulty constants
    private static final String DIFFICULTY_EASY = "easy";
    private static final String DIFFICULTY_MEDIUM = "medium";
    private static final String DIFFICULTY_HARD = "hard";
    
    // Level sizes
    private static final int EASY_SIZE = 5;
    private static final int MEDIUM_SIZE = 8;
    private static final int HARD_SIZE = 12;

    // Predefined puzzle patterns
    private static final boolean[][] EASY_PATTERN = {
            {true, false, true, false, true},
            {false, true, true, true, false},
            {false, false, true, false, false},
            {false, true, true, true, false},
            {true, false, true, false, true}
    }; // X shape
    
    private static final boolean[][] MEDIUM_PATTERN = {
            {false, false, true, true, true, true, false, false},
            {false, true, true, true, true, true, true, false},
            {true, true, true, false, false, true, true, true},
            {true, true, false, false, false, false, true, true},
            {true, true, false, false, false, false, true, true},
            {true, true, true, false, false, true, true, true},
            {false, true, true, true, true, true, true, false},
            {false, false, true, true, true, true, false, false}
    }; // Heart shape
    
    private static final boolean[][] HARD_PATTERN = {
            {false, false, false, true, true, true, true, true, true, false, false, false},
            {false, false, true, true, true, true, true, true, true, true, false, false},
            {false, true, true, true, false, false, false, false, true, true, true, false},
            {true, true, true, false, false, false, false, false, false, true, true, true},
            {true, true, false, false, false, false, false, false, false, false, true, true},
            {true, true, false, false, false, true, true, false, false, false, true, true},
            {true, true, false, false, false, true, true, false, false, false, true, true},
            {true, true, false, false, false, false, false, false, false, false, true, true},
            {true, true, true, false, false, false, false, false, false, true, true, true},
            {false, true, true, true, false, false, false, false, true, true, true, false},
            {false, false, true, true, true, true, true, true, true, true, false, false},
            {false, false, false, true, true, true, true, true, true, false, false, false}
    }; // Diamond shape

    // ANSI color codes
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_BOLD = "\u001B[1m";

    // Symbols for rendering
    private static final String EMPTY_CELL = "□";
    private static final String MARKED_CELL = "■";
    private static final String HORIZONTAL_LINE = "─";
    private static final String VERTICAL_LINE = "│";
    private static final String TOP_LEFT = "┌";
    private static final String TOP_RIGHT = "┐";
    private static final String BOTTOM_LEFT = "└";
    private static final String BOTTOM_RIGHT = "┘";
    private static final String HEART = "♥";
    
    
    // Creates a new ConsoleUI for a given puzzle field
    public ConsoleUI() {
    }
    
//    // Creates a new ConsoleUI for a given puzzle field and score service
//    public ConsoleUI(PuzzleField field, ScoreService scoreService) {
//        initializeGame(field);
//        this.scoreService = scoreService;
//    }
//
//    // Creates a new ConsoleUI with all services
//    public ConsoleUI(PuzzleField field, ScoreService scoreService, CommentService commentService, RatingService ratingService) {
//        initializeGame(field);
//        this.scoreService = scoreService;
//        this.commentService = commentService;
//        this.ratingService = ratingService;
//    }
    
    // Initializes the game with a given field
    private void initializeGame(PuzzleField field) {
        if (field != null) {
            this.field = field;
            this.upperLegend = field.getUpperLegend().getLegend();
            this.leftLegend = field.getLeftLegend().getLegend();
            this.maxLeftLegendWidth = calculateMaxLeftLegendWidth();
            this.lives = new Lives(3); // Starting with 3 lives
        }
    }
    
    // Starts the application with the menu system
    public void play() {
        showMainMenu();
    }

    // Main game loop that renders the field and processes user input until game completion
    public void startGame() {
        clearScreen();
        
        String header = "=== NONOGRAM ===";
        
        // Center the header
        int leftPadding = maxLeftLegendWidth + 1;
        int gameWidth = field.getColumns() * 3;
        int headerOffset = (gameWidth / 2) - (header.length() / 2);
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, " ".repeat(leftPadding + headerOffset) + header, true);
        printColoredText(ANSI_CYAN, "Solve the puzzle by marking the correct cells!", true);
        
        do {
            printField();
            printLives();
            
            if (errorMessage != null) {
                printColoredText(ANSI_RED, errorMessage, true);
                errorMessage = null;
            }

            printColoredText(ANSI_CYAN, "Cell you want to mark (e.g. A1) or X to exit: ", false);
            String input = scanner.nextLine().trim().toUpperCase();
            
            if ("X".equals(input)) {
                showMainMenu();
                return;
            }

            processUserMove(input);
        } while (field.getFieldState() == FieldState.PLAYING);
        
        printField();
        printLives();
        
        // Calculate and store the final score immediately after game completion
        if (field.getFieldState() == FieldState.SOLVED) {
            finalScore = field.getScore();
        }
        
        displayGameResult();
        
        // Save score if player won and score service is available
        if (field.getFieldState() == FieldState.SOLVED && scoreService != null) {
            String difficulty = getDifficultyBySize(field.getRows());
            saveScore(difficulty);
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        showMainMenu();
    }
    
    // Clears the console screen
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    // Shows the main menu
    private void showMainMenu() {
        clearScreen();
        
        int padding = 10;
        String title = "=== NONOGRAM ===";
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, " ".repeat(padding) + title, true);
        System.out.println();
        printColoredText(ANSI_CYAN, " ".repeat(padding) + "Welcome to Nonogram, a picture logic puzzle!", true);
        printColoredText(ANSI_CYAN, " ".repeat(padding) + "Fill in cells to reveal a hidden picture.", true);
        System.out.println();
        
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "1. Start Game", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "2. Leaderboard", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "3. Comments", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "4. Rate Game", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "5. Exit", true);
        System.out.println();
        
        printColoredText(ANSI_GREEN, "Select an option: ", false);
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                showGameMenu();
                break;
            case "2":
                showLeaderboardMenu();
                break;
            case "3":
                showCommentsMenu();
                break;
            case "4":
                showRatingMenu();
                break;
            case "5":
                printColoredText(ANSI_PURPLE, "Thank you for playing!", true);
                System.exit(0);
                break;
            default:
                printColoredText(ANSI_RED, "Invalid choice. Please try again.", true);
                showMainMenu();
        }
    }
    
    // Shows the game difficulty selection menu
    private void showGameMenu() {
        clearScreen();
        
        int padding = 10;
        String title = "=== SELECT DIFFICULTY ===";
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, " ".repeat(padding) + title, true);
        System.out.println();
        
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "1. Easy (5x5)", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "2. Medium (8x8)", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "3. Hard (12x12)", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "4. Random (8x8)", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "5. Return", true);
        System.out.println();
        
        printColoredText(ANSI_GREEN, "Select an option: ", false);
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                initializeGame(new PuzzleField(EASY_PATTERN));
                startGame();
                break;
            case "2":
                initializeGame(new PuzzleField(MEDIUM_PATTERN));
                startGame();
                break;
            case "3":
                initializeGame(new PuzzleField(HARD_PATTERN));
                startGame();
                break;
            case "4":
                initializeGame(PuzzleField.createRandomField(MEDIUM_SIZE));
                startGame();
                break;
            case "5":
                showMainMenu();
                break;
            default:
                printColoredText(ANSI_RED, "Invalid choice. Please try again.", true);
                showGameMenu();
        }
    }
    
    // Shows the leaderboard difficulty selection menu
    private void showLeaderboardMenu() {
        clearScreen();
        
        int padding = 10;
        String title = "=== LEADERBOARD ===";
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, " ".repeat(padding) + title, true);
        System.out.println();
        
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "1. Easy", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "2. Medium", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "3. Hard", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "4. Return", true);
        System.out.println();
        
        printColoredText(ANSI_GREEN, "Select an option: ", false);
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                showLeaderboard(DIFFICULTY_EASY);
                break;
            case "2":
                showLeaderboard(DIFFICULTY_MEDIUM);
                break;
            case "3":
                showLeaderboard(DIFFICULTY_HARD);
                break;
            case "4":
                showMainMenu();
                break;
            default:
                printColoredText(ANSI_RED, "Invalid choice. Please try again.", true);
                showLeaderboardMenu();
        }
    }
    
    // Shows the leaderboard for a specific difficulty
    private void showLeaderboard(String difficulty) {
        clearScreen();
        
        int padding = 10;
        String title = "=== " + difficulty.toUpperCase() + " LEADERBOARD ===";
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, " ".repeat(padding) + title, true);
        System.out.println();
        
        if (scoreService == null) {
            printColoredText(ANSI_CYAN, " ".repeat(padding) + "Leaderboard function is temporarily unavailable", true);
            printColoredText(ANSI_CYAN, " ".repeat(padding) + "(database connection issue)", true);
        } else {
            try {
                var scores = scoreService.getTopScores("nonogram_" + difficulty);
                
                if (scores.isEmpty()) {
                    printColoredText(ANSI_CYAN, " ".repeat(padding) + "No scores yet. Be the first to win!", true);
                } else {
                    for (int i = 0; i < scores.size(); i++) {
                        var score = scores.get(i);
                        printColoredText(ANSI_CYAN, String.format(" ".repeat(padding) + "%d. %s %d points (%s)", 
                                i + 1, score.getPlayer(), score.getPoints(), 
                                new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(score.getPlayedOn())), 
                                true);
                    }
                }
            } catch (ScoreException e) {
                printColoredText(ANSI_RED, " ".repeat(padding) + "Error loading scores: " + e.getMessage(), true);
            }
        }
        
        System.out.println();
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "Press Enter to return", true);
        scanner.nextLine();
        showLeaderboardMenu();
    }
    
    // Gets difficulty level based on field size
    private String getDifficultyBySize(int size) {
        if (size == EASY_SIZE) {
            return DIFFICULTY_EASY;
        } else if (size == MEDIUM_SIZE) {
            return DIFFICULTY_MEDIUM;
        } else if (size == HARD_SIZE) {
            return DIFFICULTY_HARD;
        } else {
            return DIFFICULTY_MEDIUM;
        }
    }

    // Displays the game result (victory or defeat message)
    private void displayGameResult() {
        if (field.getFieldState() == FieldState.SOLVED) {
            printColoredText(ANSI_BOLD + ANSI_YELLOW, "Congratulations! You've solved the nonogram!", true);
            printColoredText(ANSI_BOLD + ANSI_YELLOW, "Your score: " + finalScore + " points", true);
        } else if (field.getFieldState() == FieldState.FAILED) {
            printColoredText(ANSI_BOLD + ANSI_RED, "You lost! You've used all your attempts.", true);
        }
        errorMessage = null;
    }

    // Displays remaining lives as hearts
    private void printLives() {
        String heartsDisplay = ANSI_RED;
        for (int i = 0; i < lives.getLives(); i++) {
            heartsDisplay += HEART + " ";
        }
        heartsDisplay += ANSI_RESET;
        
        printColoredText("", "Lives: " + heartsDisplay, true);
    }

    // Prints text with specified color and controls line breaks
    private void printColoredText(String color, String text, boolean newLine) {
        if (newLine) {
            System.out.println(color + text + ANSI_RESET);
        } else {
            System.out.print(color + text + ANSI_RESET);
        }
    }

    // Processes user's move input and returns whether the move was successful
    private void processUserMove(String input) {
        Matcher matcher = INPUT_PATTERN.matcher(input);
        
        if (!matcher.matches()) {
            errorMessage = "Invalid input format! Use format 'A1'";
            return;
        }
        
        char columnChar = matcher.group(1).charAt(0);
        int row = columnChar - 'A';
        int col = Integer.parseInt(matcher.group(2)) - 1;
        
        if (row < 0 || row >= field.getRows() || col < 0 || col >= field.getColumns()) {
            errorMessage = "Coordinates are out of bounds!";
            return;
        }
        
        boolean success = field.markTile(row, col);
        if (!success) {
            lives.decreaseLives();
            
            if (lives.hasNoLivesLeft()) {
                field.setFieldState(FieldState.FAILED);
                errorMessage = "Wrong cell! You have used all your attempts.";
            } else {
                errorMessage = "Wrong cell! Remaining attempts: " + lives.getLives();
            }
        }
    }
    
    // Renders the complete game field including legends, borders, and coordinates
    private void printField() {
        printUpperLegends();
        printBorder(TOP_LEFT, TOP_RIGHT);
        printFieldWithLegends();
        printBorder(BOTTOM_LEFT, BOTTOM_RIGHT);
        printColumnNumbers();
    }

    // Prints the upper legend with column hints
    private void printUpperLegends() {
        int maxUpperLegendHeight = 0;
        for (int col = 0; col < field.getColumns() && col < upperLegend.length; col++) {
            int count = 0;
            for (int hint : upperLegend[col]) {
                if (hint > 0) count++;
            }
            maxUpperLegendHeight = Math.max(maxUpperLegendHeight, count);
        }

        if (maxUpperLegendHeight == 0) return;

        for (int row = 0; row < maxUpperLegendHeight; row++) {
            System.out.print(" ".repeat(maxLeftLegendWidth + 1));

            for (int col = 0; col < field.getColumns() && col < upperLegend.length; col++) {
                if (row < upperLegend[col].length && upperLegend[col][row] > 0) {
                    String hint = String.valueOf(upperLegend[col][row]);
                    System.out.print(" ");
                    printColoredText(ANSI_CYAN, hint, false);
                    if (hint.length() == 1) {
                        System.out.print(" ");
                    }
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    // Calculates maximum width needed for left legends
    private int calculateMaxLeftLegendWidth() {
        int maxHintsPerRow = leftLegend[0].length;
        
        // Find the maximum digit length among all hints
        int maxDigitLength = 1; // Minimum digit length is 1
        for (int row = 0; row < field.getRows(); row++) {
            for (int col = 0; col < maxHintsPerRow; col++) {
                int hint = leftLegend[row][col];
                if (hint > 0) {
                    int digitLength = String.valueOf(hint).length();
                    maxDigitLength = Math.max(maxDigitLength, digitLength);
                }
            }
        }
        
        // Total width = (maximum hints per row * (digit length + space)) + additional space
        return (maxHintsPerRow * (maxDigitLength + 1)) + 1;
    }

    // Prints the field with row hints and cell contents
    private void printFieldWithLegends() {
        for (int row = 0; row < field.getRows(); row++) {
            printLeftHints(leftLegend[row]);

            printColoredText(ANSI_YELLOW, VERTICAL_LINE, false);
            for (int col = 0; col < field.getColumns(); col++) {
                String cellSymbol = field.getTile(row, col).isMarked() ? MARKED_CELL : EMPTY_CELL;
                String cellColor = field.getTile(row, col).isMarked() ? ANSI_PURPLE : ANSI_BLUE;
                printColoredText(cellColor, " " + cellSymbol + " ", false);
            }
            printColoredText(ANSI_YELLOW, VERTICAL_LINE + " ", false);
            printColoredText(ANSI_GREEN, String.valueOf((char)('A' + row)), true);
        }
    }

    // Prints left row hints with proper alignment
    private void printLeftHints(int[] rowHints) {
        String plainHints = "";
        String coloredHints = "";
        
        for (int hint : rowHints) {
            if (hint > 0) {
                plainHints += hint + " ";
                coloredHints += ANSI_CYAN + hint + ANSI_RESET + " ";
            }
        }

        // Account for ANSI codes in padding calculation
        int padding = maxLeftLegendWidth - plainHints.length();
        System.out.print(" ".repeat(Math.max(0, padding)) + coloredHints);
    }

    // Prints column numbers below the game field
    private void printColumnNumbers() {
        System.out.print(" ".repeat(maxLeftLegendWidth + 1));

        for (int col = 0; col < field.getColumns(); col++) {
            if (col + 1 < 10) {
                printColoredText(ANSI_GREEN, " " + (col + 1) + " ", false);
            } else {
                printColoredText(ANSI_GREEN, " " + (col + 1) + " ", false);
            }
        }
        System.out.println();
    }

    // Universal method for printing top or bottom borders
    private void printBorder(String leftCorner, String rightCorner) {
        System.out.print(" ".repeat(maxLeftLegendWidth));
        printColoredText(ANSI_YELLOW, leftCorner, false);
        for (int i = 0; i < field.getColumns(); i++) {
            printColoredText(ANSI_YELLOW, HORIZONTAL_LINE.repeat(3), false);
        }
        printColoredText(ANSI_YELLOW, rightCorner, true);
    }

    // Asks the player to enter their name
    private String askPlayerName() {
        printColoredText(ANSI_CYAN, "Enter your name: ", false);
        String name = scanner.nextLine().trim();
        
        // Если пользователь не ввел имя, используем имя из системы
        if (name.isEmpty()) {
            name = "Anonymous";
        }
        
        return name;
    }

    // Saves the player's score to the database with specific difficulty
    private void saveScore(String difficulty) {
        String playerName = askPlayerName();
        
        try {
            scoreService.addScore(new Score("nonogram_" + difficulty, playerName, finalScore, new Date()));
            printColoredText(ANSI_GREEN, "Your score has been saved!", true);
            
            // Show scores for this difficulty
            var scores = scoreService.getTopScores("nonogram_" + difficulty);
            printColoredText(ANSI_BOLD + ANSI_PURPLE, difficulty.toUpperCase() + " Top Scores:", true);
            printColoredText(ANSI_YELLOW, "---------------------------------------------------------------", true);
            
            if (scores.isEmpty()) {
                printColoredText(ANSI_CYAN, "No scores yet. You're the first winner!", true);
            } else {
                for (int i = 0; i < scores.size(); i++) {
                    var score = scores.get(i);
                    printColoredText(ANSI_CYAN, String.format("%d. %s %d points (%s)", 
                            i + 1, score.getPlayer(), score.getPoints(), 
                            new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(score.getPlayedOn())), 
                            true);
                }
            }
            
            printColoredText(ANSI_YELLOW, "---------------------------------------------------------------", true);
        } catch (ScoreException e) {
            printColoredText(ANSI_RED, "Error saving score: " + e.getMessage(), true);
        }
    }

    // Shows the comments menu
    private void showCommentsMenu() {
        clearScreen();
        
        int padding = 10;
        String title = "=== COMMENTS ===";
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, " ".repeat(padding) + title, true);
        System.out.println();
        
        if (commentService == null) {
            printColoredText(ANSI_RED, "Comment service is not available.", true);
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            showMainMenu();
            return;
        }
        
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "1. View Comments", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "2. Add Comment", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "3. Return to Main Menu", true);
        System.out.println();
        
        printColoredText(ANSI_GREEN, "Select an option: ", false);
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewComments();
                break;
            case "2":
                addComment();
                break;
            case "3":
                showMainMenu();
                break;
            default:
                printColoredText(ANSI_RED, "Invalid choice. Please try again.", true);
                showCommentsMenu();
        }
    }
    
    // Views all comments for the game
    private void viewComments() {
        clearScreen();
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, "=== NONOGRAM COMMENTS ===", true);
        System.out.println();
        
        try {
            var comments = commentService.getComments("nonogram");
            
            if (comments.isEmpty()) {
                printColoredText(ANSI_CYAN, "No comments yet. Be the first to comment!", true);
            } else {
                for (int i = 0; i < comments.size(); i++) {
                    var comment = comments.get(i);
                    printColoredText(ANSI_YELLOW, "---------------------------------------------------------------", true);
                    printColoredText(ANSI_BOLD + ANSI_CYAN, String.format("%d. %s (%s)", 
                            i + 1, comment.getPlayer(), 
                            new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(comment.getCommentedOn())), 
                            true);
                    printColoredText(ANSI_CYAN, comment.getComment(), true);
                }
                printColoredText(ANSI_YELLOW, "---------------------------------------------------------------", true);
            }
        } catch (CommentException e) {
            printColoredText(ANSI_RED, "Error retrieving comments: " + e.getMessage(), true);
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        showCommentsMenu();
    }
    
    // Adds a new comment for the game
    private void addComment() {
        clearScreen();
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, "=== ADD COMMENT ===", true);
        System.out.println();
        
        String playerName = askPlayerName();
        
        printColoredText(ANSI_CYAN, "Enter your comment (or leave empty to cancel):", true);
        String commentText = scanner.nextLine().trim();
        
        if (commentText.isEmpty()) {
            showCommentsMenu();
            return;
        }
        
        try {
            commentService.addComment(new Comment("nonogram", playerName, commentText, new Date()));
            printColoredText(ANSI_GREEN, "Your comment has been added successfully!", true);
        } catch (CommentException e) {
            printColoredText(ANSI_RED, "Error adding comment: " + e.getMessage(), true);
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        showCommentsMenu();
    }
    
    // Shows the rating menu
    private void showRatingMenu() {
        clearScreen();
        
        int padding = 10;
        String title = "=== RATE GAME ===";
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, " ".repeat(padding) + title, true);
        System.out.println();
        
        if (ratingService == null) {
            printColoredText(ANSI_RED, "Rating service is not available.", true);
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            showMainMenu();
            return;
        }
        
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "1. View Ratings", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "2. Add Rating", true);
        printColoredText(ANSI_YELLOW, " ".repeat(padding) + "3. Return to Main Menu", true);
        System.out.println();
        
        printColoredText(ANSI_GREEN, "Select an option: ", false);
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewRating();
                break;
            case "2":
                addRating();
                break;
            case "3":
                showMainMenu();
                break;
            default:
                printColoredText(ANSI_RED, "Invalid choice. Please try again.", true);
                showRatingMenu();
        }
    }
    
    // Displays current game ratings
    private void viewRating() {
        clearScreen();
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, "=== CURRENT RATINGS ===", true);
        System.out.println();
        
        try {
            int avgRating = ratingService.getAverageRating("nonogram");
            printColoredText(ANSI_CYAN, "Average game rating: " + (avgRating > 0 ? avgRating + "/5" : "No ratings yet"), true);
        } catch (RatingException e) {
            printColoredText(ANSI_RED, "Error retrieving ratings: " + e.getMessage(), true);
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        showRatingMenu();
    }
    
    // Adds a new rating for the game
    private void addRating() {
        clearScreen();
        
        printColoredText(ANSI_BOLD + ANSI_PURPLE, "=== ADD RATING ===", true);
        System.out.println();
        
        String playerName = askPlayerName();
        
        try {
            int userRating = ratingService.getRating("nonogram", playerName);
            
            if (userRating > 0) {
                printColoredText(ANSI_CYAN, "Your current rating: " + userRating + "/5", true);
            }
            
            printColoredText(ANSI_CYAN, "Enter rating (1-5) or 0 to cancel: ", false);
            
            try {
                int rating = Integer.parseInt(scanner.nextLine().trim());
                
                if (rating == 0) {
                    showRatingMenu();
                    return;
                }
                
                if (rating < 1 || rating > 5) {
                    printColoredText(ANSI_RED, "Invalid rating. Please enter a number between 1 and 5.", true);
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    addRating();
                    return;
                }
                
                ratingService.setRating(new Rating("nonogram", playerName, rating, new Date()));
                printColoredText(ANSI_GREEN, "Thank you for your rating!", true);
                
            } catch (NumberFormatException e) {
                printColoredText(ANSI_RED, "Invalid input. Please enter a number.", true);
            }
            
        } catch (RatingException e) {
            printColoredText(ANSI_RED, "Error with rating: " + e.getMessage(), true);
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        showRatingMenu();
    }
}
