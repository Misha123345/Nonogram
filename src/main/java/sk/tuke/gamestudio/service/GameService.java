package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.SavedGame;
import sk.tuke.gamestudio.game.core.FieldState;
import sk.tuke.gamestudio.game.core.PuzzleField;

public interface GameService {
    PuzzleField createNewGame(int size, int livesCount, String player) throws GameException;
    PuzzleField getGameState(String player) throws GameException;
    PuzzleField makeMove(int row, int col, String player) throws GameException;
    FieldState getFieldState(String player) throws GameException;
    void saveGame(String player, long timeElapsed) throws GameException;
    void saveGame(String player, long timeElapsed, String originalDifficulty) throws GameException;
    PuzzleField loadSavedGame(String player) throws GameException;
    void deleteSavedGame(String player) throws GameException;
    boolean hasSavedGame(String player) throws GameException;
    void reset() throws GameException;
    SavedGame getSavedGameEntity(String player) throws GameException;
}