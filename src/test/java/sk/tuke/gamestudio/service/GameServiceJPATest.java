package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sk.tuke.gamestudio.entity.SavedGame;
import sk.tuke.gamestudio.game.core.FieldState;
import sk.tuke.gamestudio.game.core.PuzzleField;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class GameServiceJPATest {

    @Autowired
    private EntityManager entityManager;
    
    private GameServiceJPA gameService;
    private final String testPlayer = "testPlayer";
    
    @BeforeEach
    public void setup() {
        gameService = new GameServiceJPA();
        injectEntityManager(gameService);
    }

    @Test
    public void testCreateNewGame() {
        PuzzleField field = gameService.createNewGame(5, 3, testPlayer);
        
        assertNotNull(field);
        assertEquals(5, field.getRows());
        assertEquals(5, field.getColumns());
        assertEquals(3, field.getLives().getLives());
        assertEquals(FieldState.PLAYING, field.getFieldState());
    }
    
    @Test
    public void testGetGameState() {
        // First create a game
        PuzzleField createdField = gameService.createNewGame(5, 3, testPlayer);
        
        // Then get its state
        PuzzleField retrievedField = gameService.getGameState(testPlayer);
        
        assertNotNull(retrievedField);
        assertEquals(createdField.getRows(), retrievedField.getRows());
        assertEquals(createdField.getColumns(), retrievedField.getColumns());
        assertEquals(createdField.getLives().getLives(), retrievedField.getLives().getLives());
        assertEquals(createdField.getFieldState(), retrievedField.getFieldState());
    }
    
    @Test
    public void testMakeMove() {
        // Create a game
        PuzzleField field = gameService.createNewGame(5, 3, testPlayer);
        
        // Make a move
        int initialLives = field.getLives().getLives();
        
        // Since we don't know solution, let's make a move and observe behavior
        PuzzleField afterMove = gameService.makeMove(0, 0, testPlayer);
        
        assertNotNull(afterMove);
        
        // Either the move was valid (same lives) or invalid (lives decreased)
        assertTrue(afterMove.getLives().getLives() <= initialLives);
        
        // Field state should still be PLAYING as we have more lives
        assertEquals(FieldState.PLAYING, afterMove.getFieldState());
    }
    
    @Test
    public void testSaveGame() {
        // Create a game
        PuzzleField originalField = gameService.createNewGame(5, 3, testPlayer);
        
        // Make a move
        gameService.makeMove(0, 0, testPlayer);
        
        // Save the game
        gameService.saveGame(testPlayer, 120L, "MEDIUM");
        
        entityManager.flush();
        entityManager.clear();
        
        // Check the saved entity
        SavedGame savedGame = gameService.getSavedGameEntity(testPlayer);
        assertNotNull(savedGame);
        assertEquals(120L, savedGame.getTimeElapsed());
        assertEquals("MEDIUM", savedGame.getOriginalDifficulty());
        assertEquals(testPlayer, savedGame.getPlayer());
        assertEquals(5, savedGame.getSize());
        assertEquals(FieldState.PLAYING.name(), savedGame.getFieldState());
    }
    
    @Test
    public void testLoadSavedGame() {
        // Create a game and save it
        PuzzleField originalField = gameService.createNewGame(5, 3, testPlayer);
        gameService.makeMove(0, 0, testPlayer);
        gameService.saveGame(testPlayer, 120L, "MEDIUM");
        
        entityManager.flush();
        entityManager.clear();
        
        // Load the game
        PuzzleField loadedField = gameService.loadSavedGame(testPlayer);
        
        // Verify loaded game
        assertNotNull(loadedField);
        assertEquals(5, loadedField.getRows());
        assertEquals(5, loadedField.getColumns());
        assertEquals(FieldState.PLAYING, loadedField.getFieldState());
        
        // Verify we can also access the loaded game from memory
        PuzzleField gameState = gameService.getGameState(testPlayer);
        assertNotNull(gameState);
        assertEquals(loadedField.getRows(), gameState.getRows());
        assertEquals(loadedField.getColumns(), gameState.getColumns());
    }
    
    @Test
    public void testHasAndDeleteSavedGame() {
        // Create and save a game
        gameService.createNewGame(5, 3, testPlayer);
        gameService.saveGame(testPlayer, 60L);
        
        entityManager.flush();
        entityManager.clear();
        
        // Check if saved game exists
        boolean hasSaved = gameService.hasSavedGame(testPlayer);
        assertTrue(hasSaved);
        
        // Delete the saved game
        gameService.deleteSavedGame(testPlayer);
        
        entityManager.flush();
        entityManager.clear();
        
        // Check if saved game is deleted
        hasSaved = gameService.hasSavedGame(testPlayer);
        assertFalse(hasSaved);
    }
    
    @Test
    public void testReset() {
        // Create and save games for multiple players
        gameService.createNewGame(5, 3, testPlayer);
        gameService.saveGame(testPlayer, 60L);
        
        gameService.createNewGame(5, 3, "anotherPlayer");
        gameService.saveGame("anotherPlayer", 60L);
        
        entityManager.flush();
        entityManager.clear();
        
        // Reset all games
        gameService.reset();
        
        entityManager.flush();
        entityManager.clear();
        
        // Check if all saved games are deleted
        assertFalse(gameService.hasSavedGame(testPlayer));
        assertFalse(gameService.hasSavedGame("anotherPlayer"));
    }
    
    private void injectEntityManager(GameServiceJPA service) {
        try {
            var field = GameServiceJPA.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(service, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 