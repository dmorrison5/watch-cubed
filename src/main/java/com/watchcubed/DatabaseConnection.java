package com.watchcubed;

import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:watchcubed.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initializeTables() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        // Create titles table
        String createTitlesTable = "CREATE TABLE IF NOT EXISTS titles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL UNIQUE, " +
                "type TEXT NOT NULL, " +
                "year INTEGER, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        stmt.execute(createTitlesTable);

        // Create reviews table
        String createReviewsTable = "CREATE TABLE IF NOT EXISTS reviews (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title_id INTEGER NOT NULL, " +
                "rating INTEGER NOT NULL, " +
                "status TEXT NOT NULL, " +
                "review_text TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (title_id) REFERENCES titles(id) ON DELETE CASCADE, " +
                "UNIQUE(title_id))";
        stmt.execute(createReviewsTable);

        // Create watchlist table
        String createWatchlistTable = "CREATE TABLE IF NOT EXISTS watchlist (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title_id INTEGER NOT NULL, " +
                "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (title_id) REFERENCES titles(id) ON DELETE CASCADE, " +
                "UNIQUE(title_id))";
        stmt.execute(createWatchlistTable);

        stmt.close();
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
