package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;

import java.util.Arrays;
import java.util.List;

public class RatingServiceRestClient implements RatingService {
    private final String url = "http://localhost:8080/api/rating";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void setRating(Rating rating) throws RatingException {
        try {
            restTemplate.postForEntity(url, rating, Void.class);
        } catch (Exception e) {
            throw new RatingException("Error setting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try {
            return restTemplate.getForObject(url + "/" + game, Integer.class);
        } catch (Exception e) {
            throw new RatingException("Error getting average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            return restTemplate.getForObject(url + "/" + game + "/" + player, Integer.class);
        } catch (Exception e) {
            throw new RatingException("Error getting player rating", e);
        }
    }

    @Override
    public List<Rating> getAllRatings(String game) throws RatingException {
        try {
            Rating[] ratings = restTemplate.getForObject(url + "/all/" + game, Rating[].class);
            return Arrays.asList(ratings != null ? ratings : new Rating[0]);
        } catch (Exception e) {
            throw new RatingException("Error getting all ratings", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        throw new UnsupportedOperationException("Reset is not supported via REST");
    }
}
