package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "123";
    public static final String SELECT = "SELECT rating FROM rating WHERE game = ? AND player = ?";
    public static final String SELECT_AVG = "SELECT AVG(rating) FROM rating WHERE game = ?";
    public static final String DELETE = "DELETE FROM rating";
    public static final String INSERT = "INSERT INTO rating (game, player, rating, rated_on) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE rating SET rating = ?, rated_on = ? WHERE game = ? AND player = ?";
    public static final String SELECT_ALL = "SELECT game, player, rating, rated_on FROM rating WHERE game = ? ORDER BY rated_on DESC LIMIT 20";

    @Override
    public void setRating(Rating rating) throws RatingException {
        if (rating.getRating() < 1 || rating.getRating() > 5) {
            throw new RatingException("Rating value must be between 1 and 5");
        }
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT)
        ) {
            statement.setString(1, rating.getGame());
            statement.setString(2, rating.getPlayer());
            
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // Rating exists, update it
                    try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                        updateStatement.setInt(1, rating.getRating());
                        updateStatement.setTimestamp(2, new Timestamp(new Date().getTime()));
                        updateStatement.setString(3, rating.getGame());
                        updateStatement.setString(4, rating.getPlayer());
                        updateStatement.executeUpdate();
                    }
                } else {
                    // Rating doesn't exist, insert new one
                    try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                        insertStatement.setString(1, rating.getGame());
                        insertStatement.setString(2, rating.getPlayer());
                        insertStatement.setInt(3, rating.getRating());
                        insertStatement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));
                        insertStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem setting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVG)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    double avgRating = rs.getDouble(1);
                    return avgRating > 0 ? (int) Math.round(avgRating) : 0;
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem getting average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT)
        ) {
            statement.setString(1, game);
            statement.setString(2, player);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem getting rating", e);
        }
    }

    @Override
    public List<Rating> getAllRatings(String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Rating> ratings = new ArrayList<>();
                while (rs.next()) {
                    Rating rating = new Rating(
                        rs.getString("game"),
                        rs.getString("player"),
                        rs.getInt("rating"),
                        rs.getTimestamp("rated_on")
                    );
                    ratings.add(rating);
                }
                return ratings;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem getting all ratings", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem resetting ratings", e);
        }
    }
} 