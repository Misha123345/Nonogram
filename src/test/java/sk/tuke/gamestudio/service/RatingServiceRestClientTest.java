package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceRestClientTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    RatingServiceRestClient ratingService;

    private Date now() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    void setRating_Success() {
        Rating rating = new Rating("game", "player", 5, now());
        assertDoesNotThrow(() -> ratingService.setRating(rating));
        verify(restTemplate).postForEntity("http://localhost:8080/api/rating", rating, Void.class);
    }

    @Test
    void setRating_ThrowsException() {
        doThrow(RuntimeException.class).when(restTemplate).postForEntity(anyString(), any(), any());
        assertThrows(RatingException.class, () -> ratingService.setRating(new Rating()));
    }

    @Test
    void getAverageRating_ReturnsValue() {
        when(restTemplate.getForObject("http://localhost:8080/api/rating/game", Integer.class))
                .thenReturn(4);
        assertEquals(4, ratingService.getAverageRating("game"));
    }

    @Test
    void getAverageRating_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(Integer.class))).thenThrow(RuntimeException.class);
        assertThrows(RatingException.class, () -> ratingService.getAverageRating("game"));
    }

    @Test
    void getRating_ReturnsValue() {
        when(restTemplate.getForObject("http://localhost:8080/api/rating/game/player", Integer.class))
                .thenReturn(3);
        assertEquals(3, ratingService.getRating("game", "player"));
    }

    @Test
    void getRating_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(Integer.class))).thenThrow(RuntimeException.class);
        assertThrows(RatingException.class, () -> ratingService.getRating("game", "player"));
    }

    @Test
    void reset_ThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> ratingService.reset());
    }
}
