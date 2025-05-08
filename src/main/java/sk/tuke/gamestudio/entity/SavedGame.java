package sk.tuke.gamestudio.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "saved_game")
@NamedQuery(name = "SavedGame.findByPlayer", 
        query = "SELECT g FROM SavedGame g WHERE g.player = :player")
@NamedQuery(name = "SavedGame.deleteByPlayer", 
        query = "DELETE FROM SavedGame g WHERE g.player = :player")
@NamedQuery(name = "SavedGame.reset",
        query = "DELETE FROM SavedGame")
public class SavedGame implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "player", nullable = false)
    private String player;
    
    @Column(name = "saved_on", nullable = false)
    private Date savedOn;
    
    @Column(name = "field_state", nullable = false)
    private String fieldState;
    
    @Column(name = "field_data", nullable = false, length = 10000)
    private String fieldData;
    
    @Column(name = "time_elapsed", nullable = false)
    private Long timeElapsed;
    
    @Column(name = "size", nullable = false)
    private Integer size;
    
    @Column(name = "lives_count", nullable = false)
    private Integer livesCount;
    
    @Column(name = "original_difficulty", nullable = true)
    private String originalDifficulty;
    
    public SavedGame() {}
    
    public SavedGame(String player, String fieldState, String fieldData, Long timeElapsed, 
                     Integer size, Integer livesCount) {
        this.player = player;
        this.savedOn = new Date();
        this.fieldState = fieldState;
        this.fieldData = fieldData;
        this.timeElapsed = timeElapsed;
        this.size = size;
        this.livesCount = livesCount;
    }
    
    public SavedGame(String player, String fieldState, String fieldData, Long timeElapsed, 
                     Integer size, Integer livesCount, String originalDifficulty) {
        this(player, fieldState, fieldData, timeElapsed, size, livesCount);
        this.originalDifficulty = originalDifficulty;
    }
}