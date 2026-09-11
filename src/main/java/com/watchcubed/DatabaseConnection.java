package com.watchcubed;

import java.sql.*;

public class DatabaseConnection {
    // keeping this as a single shared connection instead of opening a new one
    // every time - that's why everything else in the app calls getConnection()
    // instead of making its own Connection object
    private static final String DB_URL = "jdbc:sqlite:watchcubed.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        // only open a new connection if we don't have one yet, or the old one
        // got closed somehow - no point reopening it every single call
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initializeTables() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        // titles table - this is the "source of truth" table, watchlist and
        // reviews both just point back to a row in here by title_id so we're
        // not duplicating name/genre/year/etc everywhere
        String createTitlesTable = "CREATE TABLE IF NOT EXISTS titles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "type TEXT NOT NULL CHECK (type IN ('Movie', 'TV Show')), " +
                "genre TEXT, " +
                "year INTEGER, " +
                "creator TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        stmt.execute(createTitlesTable);

        // watchlist table - status lives HERE, not in reviews, since you can
        // be "Watching" or "Plan to Watch" something you haven't reviewed yet.
        // keeping this separate from reviews was the whole point of splitting
        // them into two classes in the first place
        String createWatchlistTable = "CREATE TABLE IF NOT EXISTS watchlist (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title_id INTEGER NOT NULL, " +
                "status TEXT NOT NULL CHECK (status IN ('Plan to Watch', 'Watching', 'Completed')), " +
                "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (title_id) REFERENCES titles(id) ON DELETE CASCADE, " +
                "UNIQUE(title_id))";
        stmt.execute(createWatchlistTable);

        // reviews table - just rating + comment, no status here, that's
        // watchlist's job. leaving UNIQUE(title_id) off since there's no
        // reason someone couldn't review the same title more than once
        // (rewatched it, changed their mind, whatever)
        String createReviewsTable = "CREATE TABLE IF NOT EXISTS reviews (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title_id INTEGER NOT NULL, " +
                "rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5), " +
                "comment TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (title_id) REFERENCES titles(id) ON DELETE CASCADE)";
        stmt.execute(createReviewsTable);

        stmt.close();
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
