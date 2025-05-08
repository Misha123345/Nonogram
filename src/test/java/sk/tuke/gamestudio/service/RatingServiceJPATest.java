package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class RatingServiceJPATest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testSetAndGetRating() throws RatingException {
        RatingServiceJPA ratingService = new RatingServiceJPA();
        injectEntityManager(ratingService);

        Rating rating = new Rating("myGame", "tester", 4, new Date());
        ratingService.setRating(rating);

        entityManager.flush();
        entityManager.clear();

        int fetched = ratingService.getRating("myGame", "tester");
        assertEquals(4, fetched);
    }

    @Test
    public void testUpdateRating() throws RatingException {
        RatingServiceJPA ratingService = new RatingServiceJPA();
        injectEntityManager(ratingService);

        ratingService.setRating(new Rating("myGame", "tester", 3, new Date()));
        ratingService.setRating(new Rating("myGame", "tester", 5, new Date())); // update

        entityManager.flush();
        entityManager.clear();

        int updated = ratingService.getRating("myGame", "tester");
        assertEquals(5, updated);
    }

    @Test
    public void testGetAverageRating() throws RatingException {
        RatingServiceJPA ratingService = new RatingServiceJPA();
        injectEntityManager(ratingService);

        ratingService.setRating(new Rating("myGame", "user1", 3, new Date()));
        ratingService.setRating(new Rating("myGame", "user2", 5, new Date()));

        entityManager.flush();
        entityManager.clear();

        int avg = ratingService.getAverageRating("myGame");
        assertEquals(4, avg); // (3+5)/2 = 4
    }

    @Test
    public void testResetRating() throws RatingException {
        RatingServiceJPA ratingService = new RatingServiceJPA();
        injectEntityManager(ratingService);

        ratingService.setRating(new Rating("myGame", "tester", 4, new Date()));
        entityManager.flush();
        entityManager.clear();

        ratingService.reset();

        int result = ratingService.getRating("myGame", "tester");
        assertEquals(0, result);
    }

    private void injectEntityManager(RatingServiceJPA service) {
        try {
            var field = RatingServiceJPA.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(service, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
