package com.mygdx.game.utilities_resources;

import com.badlogic.gdx.Gdx;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLHelper {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/libgdxgame";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static List<String[]> getScores() {
        List<String[]> scores = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT Name, Score FROM story_scores ORDER BY Score ASC LIMIT 10");

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                int score = resultSet.getInt("Score");
                scores.add(new String[]{name, String.valueOf(score)});
            }
        } catch (SQLException e) {
            Gdx.app.error("MySQLHelper", "Error retrieving scores", e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Gdx.app.error("MySQLHelper", "Error closing database resources", e);
            }
        }

        return scores;
    }

    public static void insertScore(String name, int score) {
        String query = "INSERT INTO story_scores (Name, Score) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            Gdx.app.error("MySQLHelper", "Error inserting score", e);
        }
    }

    public static void insertArenaScore(String name, int score, int wavesCompleted) {
        String query = "INSERT INTO arena_scores (Name, Score, WavesCompleted) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.setInt(3, wavesCompleted);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            Gdx.app.error("MySQLHelper", "Error inserting arena score", e);
        }
    }

    public static List<String[]> getArenaScores() {
        List<String[]> scores = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT Name, Score, WavesCompleted FROM arena_scores ORDER BY Score DESC LIMIT 10");

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                int score = resultSet.getInt("Score");
                int waves = resultSet.getInt("WavesCompleted");
                scores.add(new String[]{name, String.valueOf(score), String.valueOf(waves)});
            }
        } catch (SQLException e) {
            Gdx.app.error("MySQLHelper", "Error retrieving arena scores", e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Gdx.app.error("MySQLHelper", "Error closing database resources", e);
            }
        }

        return scores;
    }

}
