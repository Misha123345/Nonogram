package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Score;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceRestClientTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    ScoreServiceRestClient scoreService;

    private Date now() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    void addScore_Success() {
        Score score = new Score("game", "player", 100, now());
        assertDoesNotThrow(() -> scoreService.addScore(score));
        verify(restTemplate).postForEntity("http://localhost:8080/api/score", score, Score.class);
    }

    @Test
    void addScore_ThrowsException() {
        doThrow(RuntimeException.class).when(restTemplate).postForEntity(anyString(), any(), any());
        assertThrows(ScoreException.class, () -> scoreService.addScore(new Score()));
    }

    @Test
    void getTopScores_ReturnsList() {
        Score[] mockScores = {
                new Score("game", "player1", 150, now()),
                new Score("game", "player2", 120, now())
        };
        when(restTemplate.getForObject("http://localhost:8080/api/score/game", Score[].class))
                .thenReturn(mockScores);

        List<Score> result = scoreService.getTopScores("game");
        assertEquals(2, result.size());
    }

    @Test
    void getTopScores_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(Score[].class))).thenThrow(RuntimeException.class);
        assertThrows(ScoreException.class, () -> scoreService.getTopScores("game"));
    }

    @Test
    void reset_ThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> scoreService.reset());
    }
}
