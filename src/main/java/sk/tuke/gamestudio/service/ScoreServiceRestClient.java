package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Score;

import java.util.Arrays;
import java.util.List;

public class ScoreServiceRestClient implements ScoreService {
    private final String url = "http://localhost:8080/api/score";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addScore(Score score) throws ScoreException {
        try {
            restTemplate.postForEntity(url, score, Score.class);
        } catch (Exception e) {
            throw new ScoreException("Error adding score via REST", e);
        }
    }

    @Override
    public List<Score> getTopScores(String gameName) throws ScoreException {
        try {
            Score[] scores = restTemplate.getForObject(url + "/" + gameName, Score[].class);
            return Arrays.asList(scores);
        } catch (Exception e) {
            throw new ScoreException("Error retrieving scores via REST", e);
        }
    }

    @Override
    public void reset() throws ScoreException {
        throw new UnsupportedOperationException("Reset is not supported via REST");
    }
}
