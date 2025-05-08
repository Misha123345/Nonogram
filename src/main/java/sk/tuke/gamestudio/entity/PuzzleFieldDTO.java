package sk.tuke.gamestudio.entity;

import lombok.Getter;
import lombok.Setter;
import sk.tuke.gamestudio.game.core.FieldState;
import sk.tuke.gamestudio.game.core.PuzzleField;
import sk.tuke.gamestudio.game.core.Tile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PuzzleFieldDTO {
    private List<List<Boolean>> pattern;
    private List<List<Boolean>> markedTiles;
    private int rows;
    private int columns;
    private int livesCount;
    private String fieldState;
    private long timeElapsed;
    
    // Default constructor for Jackson
    public PuzzleFieldDTO() {}
    
    // Create DTO from PuzzleField
    public static PuzzleFieldDTO fromPuzzleField(PuzzleField field, long timeElapsed) {
        PuzzleFieldDTO dto = new PuzzleFieldDTO();
        dto.rows = field.getRows();
        dto.columns = field.getColumns();
        dto.fieldState = field.getFieldState().name();
        dto.livesCount = field.getLives().getLives();
        dto.timeElapsed = timeElapsed;
        
        // Copy pattern and marked tiles
        dto.pattern = new ArrayList<>(field.getRows());
        dto.markedTiles = new ArrayList<>(field.getRows());
        
        for (int r = 0; r < field.getRows(); r++) {
            List<Boolean> patternRow = new ArrayList<>(field.getColumns());
            List<Boolean> markedRow = new ArrayList<>(field.getColumns());
            
            for (int c = 0; c < field.getColumns(); c++) {
                Tile tile = field.getTile(r, c);
                patternRow.add(tile.isSolutionTile());
                markedRow.add(tile.isMarked());
            }
            
            dto.pattern.add(patternRow);
            dto.markedTiles.add(markedRow);
        }
        
        return dto;
    }
    
    // Create PuzzleField from DTO
    public PuzzleField toPuzzleField() {
        // Convert List<List<Boolean>> to boolean[][]
        boolean[][] patternArray = new boolean[rows][columns];
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                patternArray[r][c] = pattern.get(r).get(c);
            }
        }
        
        // Рассчитываем адаптированное стартовое время, вычитая накопленное время из текущего
        long adjustedStartTime = System.currentTimeMillis() - (timeElapsed * 1000);
        
        // Используем конструктор с явным указанием стартового времени
        PuzzleField field = new PuzzleField(patternArray, livesCount, adjustedStartTime);
        
        // Restore state of marked tiles
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (markedTiles.get(r).get(c) && !field.getTile(r, c).isMarked()) {
                    field.markTile(r, c);
                }
            }
        }
        
        // If game was already solved or failed, set the state
        if (!fieldState.equals(FieldState.PLAYING.name())) {
            try {
                // Create a new instance with the same pattern but with custom field state
                final FieldState finalState = FieldState.valueOf(fieldState);
                
                // Create a simpler approach without anonymous class
                // Just construct the field, then apply moves and set the state via reflection if needed
                // For now, we'll assume the state is derived from the marked tiles
                
            } catch (IllegalArgumentException e) {
                // In case the fieldState is invalid, use the default state
                e.printStackTrace();
            }
        }
        
        return field;
    }
    
    @Override
    public String toString() {
        return "PuzzleFieldDTO{" +
                "rows=" + rows +
                ", columns=" + columns +
                ", livesCount=" + livesCount +
                ", fieldState='" + fieldState + '\'' +
                ", timeElapsed=" + timeElapsed +
                '}';
    }
} 