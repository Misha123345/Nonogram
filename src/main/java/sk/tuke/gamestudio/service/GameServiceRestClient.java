package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.SavedGame;
import sk.tuke.gamestudio.game.core.FieldState;
import sk.tuke.gamestudio.game.core.PuzzleField;

import java.util.HashMap;
import java.util.Map;

public class GameServiceRestClient implements GameService {
    private final String url = "http://localhost:8080/api/game";
    private final Map<String, PuzzleField> playerGames = new HashMap<>();
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Override
    public PuzzleField createNewGame(int size, int livesCount, String player) throws GameException {
        try {
            PuzzleField field = restTemplate.getForObject(
                url + "/new?size=" + size + "&livesCount=" + livesCount + "&player=" + player, 
                PuzzleField.class
            );
            playerGames.put(player, field);
            return field;
        } catch (Exception e) {
            throw new GameException("Error creating a new game via REST", e);
        }
    }
    
    @Override
    public PuzzleField getGameState(String player) throws GameException {
        return playerGames.get(player);
    }
    
    @Override
    public PuzzleField makeMove(int row, int col, String player) throws GameException {
        try {
            PuzzleField field = restTemplate.getForObject(
                url + "/move?row=" + row + "&col=" + col + "&player=" + player,
                PuzzleField.class
            );
            playerGames.put(player, field);
            return field;
        } catch (Exception e) {
            throw new GameException("Error making a move via REST", e);
        }
    }
    
    @Override
    public FieldState getFieldState(String player) throws GameException {
        PuzzleField field = playerGames.get(player);
        if (field == null) {
            throw new GameException("Game not found for player: " + player);
        }
        return field.getFieldState();
    }
    
    @Override
    public void reset() throws GameException {
        throw new UnsupportedOperationException("Reset is not supported via REST");
    }
    
    @Override
    public void saveGame(String player, long timeElapsed, String originalDifficulty) throws GameException {
        try {
            String requestUrl = url + "/save?player=" + player + "&timeElapsed=" + timeElapsed;
            if (originalDifficulty != null && !originalDifficulty.isEmpty()) {
                requestUrl += "&originalDifficulty=" + originalDifficulty;
            }
            restTemplate.getForObject(requestUrl, Void.class);
        } catch (Exception e) {
            throw new GameException("Error saving game via REST", e);
        }
    }
    
    @Override
    public void saveGame(String player, long timeElapsed) throws GameException {
        saveGame(player, timeElapsed, null);
    }
    
    @Override
    public PuzzleField loadSavedGame(String player) throws GameException {
        try {
            PuzzleField field = restTemplate.getForObject(
                url + "/load?player=" + player,
                PuzzleField.class
            );
            if (field != null) {
                playerGames.put(player, field);
            }
            return field;
        } catch (Exception e) {
            throw new GameException("Error loading saved game via REST", e);
        }
    }
    
    @Override
    public void deleteSavedGame(String player) throws GameException {
        try {
            restTemplate.getForObject(
                url + "/delete?player=" + player,
                Void.class
            );
        } catch (Exception e) {
            throw new GameException("Error deleting saved game via REST", e);
        }
    }
    
    @Override
    public boolean hasSavedGame(String player) throws GameException {
        try {
            Boolean result = restTemplate.getForObject(
                url + "/has?player=" + player,
                Boolean.class
            );
            return result != null && result;
        } catch (Exception e) {
            throw new GameException("Error checking for saved game via REST", e);
        }
    }
    
    @Override
    public SavedGame getSavedGameEntity(String player) throws GameException {
        throw new UnsupportedOperationException("Getting saved game entity directly is not supported via REST client");
    }
}