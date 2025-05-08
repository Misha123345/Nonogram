package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.game.core.FieldState;
import sk.tuke.gamestudio.game.core.PuzzleField;
import sk.tuke.gamestudio.game.core.Tile;
import sk.tuke.gamestudio.game.core.legend.LeftLegend;
import sk.tuke.gamestudio.game.core.legend.UpperLegend;
import sk.tuke.gamestudio.service.GameException;
import sk.tuke.gamestudio.service.GameService;
import sk.tuke.gamestudio.entity.SavedGame;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game/puzzle")
@CrossOrigin(origins = "*")
public class GameServiceRest {

    @Autowired
    private GameService gameService;

    public static class TileDTO {
        public boolean marked;

        public TileDTO(Tile tile) {
            this.marked = tile.isMarked();
        }
    }
    
    public static class LegendDTO {
        public List<List<Integer>> rows;
        public List<List<Integer>> columns;

        public LegendDTO(LeftLegend leftLegend, UpperLegend upperLegend) {
            this.rows = Arrays.stream(leftLegend.getLegend())
                .map(row -> Arrays.stream(row).boxed().collect(Collectors.toList()))
                .collect(Collectors.toList());
                
            this.columns = Arrays.stream(upperLegend.getLegend())
                .map(col -> Arrays.stream(col).boxed().collect(Collectors.toList()))
                .collect(Collectors.toList());
        }
    }


    public static class GameStateDTO {
        public int rows;
        public int columns;
        public TileDTO[][] field;
        public FieldState state;
        public int score;
        public LegendDTO legend;
        public boolean validMove;
        public int lives;
        public Long timeElapsed;
        public String originalDifficulty;

        public GameStateDTO(PuzzleField field, boolean validMove) {
            this.rows = field.getRows();
            this.columns = field.getColumns();
            this.state = field.getFieldState();
            this.score = field.getScore();
            this.legend = new LegendDTO(field.getLeftLegend(), field.getUpperLegend());
            this.validMove = validMove;
            this.lives = field.getLives().getLives();
            this.timeElapsed = null;
            this.originalDifficulty = null;

            this.field = new TileDTO[rows][columns];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    this.field[r][c] = new TileDTO(field.getTile(r, c));
                }
            }
        }
    }

    // Move request DTO
    public static class MoveRequest {
        public Integer row;
        public Integer col;
        public String originalDifficulty;
        
        public MoveRequest() {
        }
        
        public Integer getRow() {
            return row;
        }
        
        public void setRow(Integer row) {
            this.row = row;
        }
        
        public Integer getCol() {
            return col;
        }
        
        public void setCol(Integer col) {
            this.col = col;
        }
        
        public String getOriginalDifficulty() {
            return originalDifficulty;
        }
        
        public void setOriginalDifficulty(String originalDifficulty) {
            this.originalDifficulty = originalDifficulty;
        }
        
        @Override
        public String toString() {
            return "MoveRequest{row=" + row + ", col=" + col + ", originalDifficulty=" + originalDifficulty + "}";
        }
    }

    // Get current user from security context
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        // Return "anonymous" for guests
        return "anonymous";
    }

    @GetMapping("/new")
    public ResponseEntity<GameStateDTO> startNewGame(
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "3") int livesCount) {
        try {
            String username = getCurrentUsername();
            PuzzleField field = gameService.createNewGame(size, livesCount, username);
            return ResponseEntity.ok(new GameStateDTO(field, true));
        } catch (Exception e) {
            System.err.println("Error creating a new game: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/state")
    public ResponseEntity<GameStateDTO> getGameState() {
        try {
            String username = getCurrentUsername();
            PuzzleField field = gameService.getGameState(username);
            
            if (field == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(new GameStateDTO(field, true));
        } catch (Exception e) {
            System.err.println("Error getting game state: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/move")
    public ResponseEntity<GameStateDTO> makeMove(@RequestBody MoveRequest move) {
        if (move == null || move.row == null || move.col == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            String username = getCurrentUsername();
            PuzzleField field = gameService.getGameState(username);
            
            if (field == null) {
                return ResponseEntity.badRequest().build();
            }
            
            if (field.getFieldState() != FieldState.PLAYING) {
                return ResponseEntity.ok(new GameStateDTO(field, false));
            }
            
            int livesBefore = field.getLives().getLives();
            
            field = gameService.makeMove(move.row, move.col, username);
            
            int livesAfter = field.getLives().getLives();
            boolean validMove = livesAfter >= livesBefore;
            
            if (field.getFieldState() == FieldState.PLAYING) {
                long timeElapsed = (System.currentTimeMillis() - field.getStartTime()) / 1000;
                
                String originalDifficulty = move.originalDifficulty;
                
                if (originalDifficulty != null && originalDifficulty.trim().isEmpty()) {
                    originalDifficulty = null;
                }
                
                gameService.saveGame(username, timeElapsed, originalDifficulty);
            }
            
            return ResponseEntity.ok(new GameStateDTO(field, validMove));
        } catch (GameException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/save")
    public ResponseEntity<Void> saveGame(
        @RequestParam(defaultValue = "0") long timeElapsed,
        @RequestParam(required = false) String originalDifficulty) {
        try {
            String username = getCurrentUsername();
            
            // Check that originalDifficulty is not an empty string
            if (originalDifficulty != null && originalDifficulty.trim().isEmpty()) {
                originalDifficulty = null;
            }
            
            gameService.saveGame(username, timeElapsed, originalDifficulty);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/load")
    public ResponseEntity<GameStateDTO> loadSavedGame() {
        try {
            String username = getCurrentUsername();
            
            // Get saved game directly from database
            SavedGame savedGame = gameService.getSavedGameEntity(username);
            if (savedGame == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Load game field
            PuzzleField field = gameService.loadSavedGame(username);
            
            if (field == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Create DTO with field and set timeElapsed from saved game
            GameStateDTO gameStateDTO = new GameStateDTO(field, true);
            
            Long timeValue = savedGame.getTimeElapsed();
            
            // Check for null or <= 0
            if (timeValue == null || timeValue <= 0) {
                // If timeElapsed is missing or 0, set default value
                timeValue = 1L; // Set minimum value to avoid 0
            }
            
            gameStateDTO.timeElapsed = timeValue;
            gameStateDTO.originalDifficulty = savedGame.getOriginalDifficulty();
            
            return ResponseEntity.ok(gameStateDTO);
        } catch (Exception e) {
            System.err.println("Error loading saved game: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/delete")
    public ResponseEntity<Void> deleteSavedGame() {
        try {
            String username = getCurrentUsername();
            gameService.deleteSavedGame(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting saved game: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/has")
    public ResponseEntity<Boolean> hasSavedGame() {
        try {
            String username = getCurrentUsername();
            boolean hasSaved = gameService.hasSavedGame(username);
            return ResponseEntity.ok(hasSaved);
        } catch (Exception e) {
            System.err.println("Error checking for saved game: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 