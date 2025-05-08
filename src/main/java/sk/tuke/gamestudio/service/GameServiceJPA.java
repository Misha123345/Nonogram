package sk.tuke.gamestudio.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.gamestudio.entity.PuzzleFieldDTO;
import sk.tuke.gamestudio.entity.SavedGame;
import sk.tuke.gamestudio.game.core.FieldState;
import sk.tuke.gamestudio.game.core.PuzzleField;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Transactional
public class GameServiceJPA implements GameService {
    private final Map<String, PuzzleField> playerGames = new HashMap<>();
    private final ObjectMapper objectMapper;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public GameServiceJPA() {
        // Configure ObjectMapper for more flexible deserialization
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }
    
    @Override
    public PuzzleField createNewGame(int size, int livesCount, String player) {
        try {
            deleteSavedGame(player);
            
            PuzzleField field = PuzzleField.createRandomField(size, livesCount);
            playerGames.put(player, field);
            
            return field;
        } catch (Exception e) {
            throw new GameException("Error creating a new game", e);
        }
    }
    
    @Override
    public PuzzleField getGameState(String player) {
        try {
            PuzzleField field = playerGames.get(player);
            
            if (field == null) {
                return null;
            }
            
            return field;
        } catch (Exception e) {
            throw new GameException("Error getting game state", e);
        }
    }
    
    @Override
    public PuzzleField makeMove(int row, int col, String player) throws GameException {
        try {
            PuzzleField field = playerGames.get(player);
            
            if (field == null) {
                throw new GameException("Game not found for player: " + player);
            }
            
            if (field.getFieldState() != FieldState.PLAYING) {
                throw new GameException("Game is over");
            }

            field.markTile(row, col);
            
            playerGames.put(player, field);
            
            return field;
        } catch (GameException e) {
            throw e;
        } catch (Exception e) {
            throw new GameException("Error making a move", e);
        }
    }
    
    @Override
    public FieldState getFieldState(String player) {
        PuzzleField field = playerGames.get(player);
        if (field == null) {
            throw new GameException("Game not found for player: " + player);
        }
        return field.getFieldState();
    }
    
    @Override
    public void reset() {
        try {
            playerGames.clear();
            entityManager.createNamedQuery("SavedGame.reset").executeUpdate();
        } catch (Exception e) {
            throw new GameException("Error resetting games", e);
        }
    }
    
    @Override
    public void saveGame(String player, long timeElapsed) throws GameException {
        // Вызываем перегруженный метод с null в качестве originalDifficulty
        saveGame(player, timeElapsed, null);
    }
    
    @Override
    public void saveGame(String player, long timeElapsed, String originalDifficulty) throws GameException {
        try {
            PuzzleField field = playerGames.get(player);
            if (field == null || field.getFieldState() != FieldState.PLAYING) {
                return;
            }
            
            deleteSavedGame(player);
            
            // Use DTO instead of direct serialization
            PuzzleFieldDTO dto = PuzzleFieldDTO.fromPuzzleField(field, timeElapsed);
            String fieldData = objectMapper.writeValueAsString(dto);
            
            SavedGame savedGame = new SavedGame(
                player,
                field.getFieldState().name(),
                fieldData,
                timeElapsed,
                field.getRows(),
                field.getLives().getLives(),
                originalDifficulty
            );
            
            entityManager.persist(savedGame);
            
        } catch (Exception e) {
            throw new GameException("Error saving game", e);
        }
    }
    
    @Override
    public PuzzleField loadSavedGame(String player) throws GameException {
        try {
            SavedGame savedGame = getSavedGameEntity(player);
            if (savedGame == null) {
                return null;
            }
            
            // Deserialize to DTO instead of direct deserialization to PuzzleField
            PuzzleFieldDTO dto = objectMapper.readValue(savedGame.getFieldData(), PuzzleFieldDTO.class);
            PuzzleField field = dto.toPuzzleField();
            
            playerGames.put(player, field);
            
            return field;
        } catch (Exception e) {
            throw new GameException("Error loading saved game: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteSavedGame(String player) throws GameException {
        try {
            entityManager.createNamedQuery("SavedGame.deleteByPlayer")
                .setParameter("player", player)
                .executeUpdate();
        } catch (Exception e) {
            throw new GameException("Error deleting saved game", e);
        }
    }
    
    @Override
    public boolean hasSavedGame(String player) throws GameException {
        try {
            SavedGame savedGame = getSavedGameEntity(player);
            return savedGame != null;
        } catch (Exception e) {
            throw new GameException("Error checking for saved game", e);
        }
    }
    
    @Override
    public SavedGame getSavedGameEntity(String player) throws GameException {
        try {
            return entityManager.createNamedQuery("SavedGame.findByPlayer", SavedGame.class)
                .setParameter("player", player)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new GameException("Error getting saved game entity", e);
        }
    }
}