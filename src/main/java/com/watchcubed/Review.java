package com.watchcubed;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Review {
    private int id;
    private int titleId;
    private int rating;
    private String comment;

    public Review(int titleId, int rating, String comment) {
        this.titleId = titleId;
        this.rating = rating;
        this.comment = comment;
    }

    public Review(int id, int titleId, int rating, String comment) {
        this.id = id;
        this.titleId = titleId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getTitleId() {
        return titleId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    // Setters
    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Validation
    private static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    // Database operations
    public static void insertReview(String titleName, int rating, String comment) throws SQLException {
        if (!isValidRating(rating)) {
            throw new IllegalArgumentException("Invalid rating. Must be between 1 and 5.");
        }

        Title title = Title.getByName(titleName);
        if (title == null) {
            throw new IllegalArgumentException("Title not found: " + titleName);
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO reviews (title_id, rating, comment) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, title.getId());
        pstmt.setInt(2, rating);
        pstmt.setString(3, comment);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void updateRating(int newRating) throws SQLException {
        if (!isValidRating(newRating)) {
            throw new IllegalArgumentException("Invalid rating. Must be between 1 and 5.");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE reviews SET rating = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, newRating);
        pstmt.setInt(2, this.id);
        pstmt.executeUpdate();
        pstmt.close();
        this.rating = newRating;
    }

    public void updateComment(String newComment) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE reviews SET comment = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newComment);
        pstmt.setInt(2, this.id);
        pstmt.executeUpdate();
        pstmt.close();
        this.comment = newComment;
    }

    public static List<ReviewEntry> getReviewsForTitle(String titleName) throws SQLException {
        Title title = Title.getByName(titleName);
        if (title == null) {
            throw new IllegalArgumentException("Title not found: " + titleName);
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT r.id, r.title_id, r.rating, r.comment, t.name, t.type, t.year " +
                     "FROM reviews r " +
                     "JOIN titles t ON r.title_id = t.id " +
                     "WHERE r.title_id = ? " +
                     "ORDER BY r.created_at DESC";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, title.getId());
        ResultSet rs = pstmt.executeQuery();

        List<ReviewEntry> reviews = new ArrayList<>();
        while (rs.next()) {
            reviews.add(new ReviewEntry(
                    rs.getInt("id"),
                    rs.getInt("title_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("year")
            ));
        }
        rs.close();
        pstmt.close();
        return reviews;
    }

    public static List<ReviewEntry> getAllReviews() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT r.id, r.title_id, r.rating, r.comment, t.name, t.type, t.year " +
                     "FROM reviews r " +
                     "JOIN titles t ON r.title_id = t.id " +
                     "ORDER BY t.name, r.created_at DESC";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        List<ReviewEntry> reviews = new ArrayList<>();
        while (rs.next()) {
            reviews.add(new ReviewEntry(
                    rs.getInt("id"),
                    rs.getInt("title_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("year")
            ));
        }
        rs.close();
        pstmt.close();
        return reviews;
    }

    public void delete() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "DELETE FROM reviews WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, this.id);
        pstmt.executeUpdate();
        pstmt.close();
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", titleId=" + titleId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }

    // Inner class to represent a review entry with joined title data
    public static class ReviewEntry {
        private int id;
        private int titleId;
        private int rating;
        private String comment;
        private String titleName;
        private String type;
        private int year;

        public ReviewEntry(int id, int titleId, int rating, String comment, String titleName, String type, int year) {
            this.id = id;
            this.titleId = titleId;
            this.rating = rating;
            this.comment = comment;
            this.titleName = titleName;
            this.type = type;
            this.year = year;
        }

        public int getId() {
            return id;
        }

        public int getTitleId() {
            return titleId;
        }

        public int getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }

        public String getTitleName() {
            return titleName;
        }

        public String getType() {
            return type;
        }

        public int getYear() {
            return year;
        }

        @Override
        public String toString() {
            return titleName + " (" + type + ", " + year + ") - Rating: " + rating + "/5" +
                    (comment != null && !comment.isEmpty() ? " - " + comment : "");
        }
    }
}
