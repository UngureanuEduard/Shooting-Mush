package com.mygdx.game.utilities_resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper {


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
            resultSet = statement.executeQuery("SELECT Name, Score FROM scores ORDER BY Score DESC");

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                int score = resultSet.getInt("Score");
                scores.add(new String[] { name, String.valueOf(score) });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return scores;
    }

    public static void insertScore(String name, int score) {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            statement = connection.createStatement();
            String query = "INSERT INTO scores (Name, Score) VALUES ('" + name + "', " + score + ")";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
