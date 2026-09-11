package com.watchcubed;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchList {
    private int id;
    private int titleId;
    private String status;

    public WatchList(int titleId, String status) {
        this.titleId = titleId;
        this.status = status;
    }

    public WatchList(int id, int titleId, String status) {
        this.id = id;
        this.titleId = titleId;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getTitleId() {
        return titleId;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    // Validation
    private static boolean isValidStatus(String status) {
        return status.equals("Plan to Watch") || status.equals("Watching") || status.equals("Completed");
    }

    // Database operations
    public static void addToWatchList(String titleName, String status) throws SQLException {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status. Must be 'Plan to Watch', 'Watching', or 'Completed'");
        }

        Title title = Title.getByName(titleName);
        if (title == null) {
            throw new IllegalArgumentException("Title not found: " + titleName);
        }

        // Check if title is already on watch list
        Connection conn = DatabaseConnection.getConnection();
        String checkSql = "SELECT id FROM watchlist WHERE title_id = ?";
        PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
        checkPstmt.setInt(1, title.getId());
        ResultSet checkRs = checkPstmt.executeQuery();
        if (checkRs.next()) {
            checkRs.close();
            checkPstmt.close();
            throw new IllegalArgumentException(titleName + " is already on your watch list.");
        }
        checkRs.close();
        checkPstmt.close();

        String sql = "INSERT INTO watchlist (title_id, status) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, title.getId());
        pstmt.setString(2, status);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void updateStatus(String newStatus) throws SQLException {
        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Invalid status. Must be 'Plan to Watch', 'Watching', or 'Completed'");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE watchlist SET status = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newStatus);
        pstmt.setInt(2, this.id);
        pstmt.executeUpdate();
        pstmt.close();
        this.status = newStatus;
    }

    public static void updateStatusByTitleName(String titleName, String newStatus) throws SQLException {
        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Invalid status. Must be 'Plan to Watch', 'Watching', or 'Completed'");
        }

        Title title = Title.getByName(titleName);
        if (title == null) {
            throw new IllegalArgumentException("Title not found: " + titleName);
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE watchlist SET status = ? WHERE title_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newStatus);
        pstmt.setInt(2, title.getId());
        int rowsAffected = pstmt.executeUpdate();
        pstmt.close();

        if (rowsAffected == 0) {
            throw new IllegalArgumentException(titleName + " is not currently on your watch list.");
        }
    }

    public static List<WatchListEntry> getWatchList() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT w.id, w.title_id, w.status, t.name, t.type, t.genre, t.year, t.creator " +
                     "FROM watchlist w " +
                     "JOIN titles t ON w.title_id = t.id " +
                     "ORDER BY t.name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        List<WatchListEntry> entries = new ArrayList<>();
        while (rs.next()) {
            entries.add(new WatchListEntry(
                    rs.getInt("id"),
                    rs.getInt("title_id"),
                    rs.getString("status"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("genre"),
                    rs.getInt("year"),
                    rs.getString("creator")
            ));
        }
        rs.close();
        pstmt.close();
        return entries;
    }

    @Override
    public String toString() {
        return "WatchList{" +
                "id=" + id +
                ", titleId=" + titleId +
                ", status='" + status + '\'' +
                '}';
    }

    // Inner class to represent a watch list entry with joined title data
    public static class WatchListEntry {
        private int id;
        private int titleId;
        private String status;
        private String titleName;
        private String type;
        private String genre;
        private int year;
        private String creator;

        public WatchListEntry(int id, int titleId, String status, String titleName, String type, String genre, int year, String creator) {
            this.id = id;
            this.titleId = titleId;
            this.status = status;
            this.titleName = titleName;
            this.type = type;
            this.genre = genre;
            this.year = year;
            this.creator = creator;
        }

        public int getId() {
            return id;
        }

        public int getTitleId() {
            return titleId;
        }

        public String getStatus() {
            return status;
        }

        public String getTitleName() {
            return titleName;
        }

        public String getType() {
            return type;
        }

        public String getGenre() {
            return genre;
        }

        public int getYear() {
            return year;
        }

        public String getCreator() {
            return creator;
        }

        @Override
        public String toString() {
            return titleName + " (" + type + ", " + year + ") - " + status;
        }
    }
}
