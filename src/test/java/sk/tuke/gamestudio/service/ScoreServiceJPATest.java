package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sk.tuke.gamestudio.entity.Score;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class ScoreServiceJPATest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testAddAndGetTopScores() throws ScoreException {
        ScoreServiceJPA scoreService = new ScoreServiceJPA();
        injectEntityManager(scoreService);

        scoreService.addScore(new Score("myGame", "tester1", 100, new Date()));
        scoreService.addScore(new Score("myGame", "tester2", 200, new Date()));

        entityManager.flush();
        entityManager.clear();

        List<Score> topScores = scoreService.getTopScores("myGame");
        assertEquals(2, topScores.size());
        assertEquals(200, topScores.get(0).getPoints()); // Assuming ordered DESC
    }

    @Test
    public void testResetScores() throws ScoreException {
        ScoreServiceJPA scoreService = new ScoreServiceJPA();
        injectEntityManager(scoreService);

        scoreService.addScore(new Score("myGame", "tester", 123, new Date()));
        entityManager.flush();
        entityManager.clear();

        scoreService.reset();

        List<Score> scores = scoreService.getTopScores("myGame");
        assertTrue(scores.isEmpty());
    }

    private void injectEntityManager(ScoreServiceJPA service) {
        try {
            var field = ScoreServiceJPA.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(service, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
