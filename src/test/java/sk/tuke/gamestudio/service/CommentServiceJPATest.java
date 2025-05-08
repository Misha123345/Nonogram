package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sk.tuke.gamestudio.entity.Comment;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class CommentServiceJPATest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testAddAndGetComments() throws CommentException {
        CommentServiceJPA commentService = new CommentServiceJPA();
        injectEntityManager(commentService);

        Comment comment = new Comment("myGame", "tester", "Nice game!", new Date());
        commentService.addComment(comment);

        // flush & clear to simulate commit
        entityManager.flush();
        entityManager.clear();

        List<Comment> comments = commentService.getComments("myGame");
        assertEquals(1, comments.size());
        assertEquals("Nice game!", comments.get(0).getComment());
    }

    @Test
    public void testReset() throws CommentException {
        CommentServiceJPA commentService = new CommentServiceJPA();
        injectEntityManager(commentService);

        commentService.addComment(new Comment("game1", "user1", "Comment1", new Date()));
        commentService.addComment(new Comment("game1", "user2", "Comment2", new Date()));

        entityManager.flush();
        entityManager.clear();

        commentService.reset();

        List<Comment> comments = commentService.getComments("game1");
        assertTrue(comments.isEmpty());
    }

    // Хелпер для установки EntityManager (Spring не делает это для "ручного" бин-а)
    private void injectEntityManager(CommentServiceJPA service) {
        try {
            var field = CommentServiceJPA.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(service, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
