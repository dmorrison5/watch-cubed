package com.watchcubed;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Title {
    private int id;
    private String name;
    private String type;
    private String genre;
    private int year;
    private String creator;

    public Title(String name, String type, String genre, int year, String creator) {
        this.name = name;
        this.type = type;
        this.genre = genre;
        this.year = year;
        this.creator = creator;
    }

    public Title(int id, String name, String type, String genre, int year, String creator) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.genre = genre;
        this.year = year;
        this.creator = creator;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    // Database operations
    public void save() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO titles (name, type, genre, year, creator) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, this.name);
        pstmt.setString(2, this.type);
        pstmt.setString(3, this.genre);
        pstmt.setInt(4, this.year);
        pstmt.setString(5, this.creator);
        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            this.id = rs.getInt(1);
        }
        pstmt.close();
    }

    public static Title getByName(String name) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM titles WHERE name = ? COLLATE NOCASE";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();

        Title title = null;
        if (rs.next()) {
            title = new Title(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("genre"),
                    rs.getInt("year"),
                    rs.getString("creator")
            );
        }
        pstmt.close();
        return title;
    }

    public static boolean existsByName(String name) throws SQLException {
        return getByName(name) != null;
    }

    public static List<Title> getAllTitles() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM titles ORDER BY name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        List<Title> titles = new ArrayList<>();
        while (rs.next()) {
            titles.add(new Title(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("genre"),
                    rs.getInt("year"),
                    rs.getString("creator")
            ));
        }
        pstmt.close();
        return titles;
    }

    public void update() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE titles SET name = ?, type = ?, genre = ?, year = ?, creator = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, this.name);
        pstmt.setString(2, this.type);
        pstmt.setString(3, this.genre);
        pstmt.setInt(4, this.year);
        pstmt.setString(5, this.creator);
        pstmt.setInt(6, this.id);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void delete() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "DELETE FROM titles WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, this.id);
        pstmt.executeUpdate();
        pstmt.close();
    }

    @Override
    public String toString() {
        return "Title{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                ", creator='" + creator + '\'' +
                '}';
    }
}
