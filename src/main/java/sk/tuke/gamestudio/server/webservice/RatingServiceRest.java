package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("api/rating")
@CrossOrigin(origins = "*")
public class RatingServiceRest {
    @Autowired
    private RatingService ratingService;

    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player) {
        return ratingService.getRating(game, player);
    }

    @GetMapping("/{game}")
    public Integer getAverageRating(@PathVariable String game) {
        return ratingService.getAverageRating(game);
    }
    
    @GetMapping("/all/{game}")
    public List<Rating> getAllRatings(@PathVariable String game) {
        return ratingService.getAllRatings(game);
    }

    @PostMapping
    public void setRating(@RequestBody Rating rating) {
        ratingService.setRating(rating);
    }
}
