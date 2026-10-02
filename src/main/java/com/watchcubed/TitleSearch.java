package com.watchcubed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TitleSearch {
    public static List<Title> searchByName(String name) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, name, type, genre, year, creator FROM titles WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, "%" + name + "%");
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

        rs.close();
        pstmt.close();
        return titles;
    }

    public static List<Title> searchByCreator(String creator) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, name, type, genre, year, creator FROM titles WHERE LOWER(creator) LIKE LOWER(?) ORDER BY name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, "%" + creator + "%");
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

        rs.close();
        pstmt.close();
        return titles;
    }

    public static List<Title> searchByGenre(String genre) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, name, type, genre, year, creator FROM titles WHERE LOWER(genre) LIKE LOWER(?) ORDER BY name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, "%" + genre + "%");
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

        rs.close();
        pstmt.close();
        return titles;
    }

    public static List<Title> getTitlesByYear(int year) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, name, type, genre, year, creator FROM titles WHERE year = ? ORDER BY name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, year);
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

        rs.close();
        pstmt.close();
        return titles;
    }
}
