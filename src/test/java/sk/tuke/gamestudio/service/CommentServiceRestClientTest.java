package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Comment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceRestClientTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    CommentServiceRestClient commentService;

    private Date now() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    @DisplayName("addComment: успешно отправляет комментарий")
    void addComment_Success() {
        Comment comment = new Comment("game", "player", "text", now());
        assertDoesNotThrow(() -> commentService.addComment(comment));
        verify(restTemplate).postForEntity("http://localhost:8080/api/comment", comment, Void.class);
    }

    @Test
    @DisplayName("addComment: при ошибке выбрасывает CommentException")
    void addComment_ThrowsException() {
        Comment comment = new Comment("game", "player", "text", now());
        doThrow(RuntimeException.class).when(restTemplate).postForEntity(anyString(), any(), any());
        assertThrows(CommentException.class, () -> commentService.addComment(comment));
    }

    @Test
    @DisplayName("getComments: возвращает список комментариев")
    void getComments_ReturnsList() {
        Comment[] mockComments = {
                new Comment("game", "player1", "Nice!", now()),
                new Comment("game", "player2", "Cool!", now())
        };
        when(restTemplate.getForObject("http://localhost:8080/api/comment/game", Comment[].class))
                .thenReturn(mockComments);

        List<Comment> result = commentService.getComments("game");
        assertEquals(2, result.size());
    }

    @Test
    void getComments_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(Comment[].class))).thenThrow(RuntimeException.class);
        assertThrows(CommentException.class, () -> commentService.getComments("game"));
    }

    @Test
    void reset_ThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> commentService.reset());
    }
}
