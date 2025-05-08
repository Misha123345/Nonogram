package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        try {
            Rating existing = entityManager
                    .createQuery("SELECT r FROM Rating r WHERE r.game = :game AND r.player = :player", Rating.class)
                    .setParameter("game", rating.getGame())
                    .setParameter("player", rating.getPlayer())
                    .getSingleResult();

            existing.setRating(rating.getRating());
            existing.setRatedOn(new Date());
            entityManager.merge(existing);
        } catch (NoResultException e) {
            rating.setRatedOn(new Date());
            entityManager.persist(rating);
        } catch (Exception e) {
            throw new RatingException("Error setting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try {
            Double avg = entityManager
                    .createNamedQuery("Rating.getAverageRating", Double.class)
                    .setParameter("game", game)
                    .getSingleResult();
            return avg != null ? (int) Math.round(avg) : 0;
        } catch (Exception e) {
            throw new RatingException("Error getting average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            return entityManager
                    .createNamedQuery("Rating.getRating", Integer.class)
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            throw new RatingException("Error getting rating", e);
        }
    }

    @Override
    public List<Rating> getAllRatings(String game) throws RatingException {
        try {
            return entityManager
                    .createQuery("SELECT r FROM Rating r WHERE r.game = :game ORDER BY r.ratedOn DESC", Rating.class)
                    .setParameter("game", game)
                    .setMaxResults(20) // Ограничение количества возвращаемых результатов
                    .getResultList();
        } catch (Exception e) {
            throw new RatingException("Error getting all ratings", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        try {
            entityManager.createNamedQuery("Rating.resetRating").executeUpdate();
        } catch (Exception e) {
            throw new RatingException("Error resetting ratings", e);
        }
    }
}
