package sk.tuke.gamestudio.game;

import sk.tuke.gamestudio.game.consoleui.ConsoleUI;

public class Main {
    
    public static void main(String[] args) {
//      ScoreService scoreService = new ScoreServiceJDBC();
//      CommentService commentService = new CommentServiceJDBC();
//      RatingService ratingService = new RatingServiceJDBC();
        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.play();
    }
}
