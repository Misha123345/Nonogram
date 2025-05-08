package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Comment;

import java.util.Arrays;
import java.util.List;

public class CommentServiceRestClient implements CommentService {
    private final String url = "http://localhost:8080/api/comment";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addComment(Comment comment) throws CommentException {
        try {
            restTemplate.postForEntity(url, comment, Void.class);
        } catch (Exception e) {
            throw new CommentException("Error adding comment via REST", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        try {
            Comment[] comments = restTemplate.getForObject(url + "/" + game, Comment[].class);
            return Arrays.asList(comments);
        } catch (Exception e) {
            throw new CommentException("Error retrieving comments via REST", e);
        }
    }

    @Override
    public void reset() throws CommentException {
        throw new UnsupportedOperationException("Reset is not supported via REST");
    }
}
