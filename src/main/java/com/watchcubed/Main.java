package com.watchcubed;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static boolean running = true;

    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeTables();
            showMenu();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            try {
                DatabaseConnection.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {
        while (running) {
            System.out.println("\n=== Watch Cubed ===");
            System.out.println("1. Add title");
            System.out.println("2. Add to watch list");
            System.out.println("3. Update watch status");
            System.out.println("4. Add review");
            System.out.println("5. View all titles");
            System.out.println("6. View watch list");
            System.out.println("7. View reviews");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        addTitle();
                        break;
                    case "2":
                        addToWatchList();
                        break;
                    case "3":
                        updateWatchStatus();
                        break;
                    case "4":
                        addReview();
                        break;
                    case "5":
                        viewAllTitles();
                        break;
                    case "6":
                        viewWatchList();
                        break;
                    case "7":
                        viewReviews();
                        break;
                    case "8":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }
        }
    }

    private static void addTitle() throws SQLException {
        System.out.print("Title name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Title name cannot be blank.");
        }

        System.out.print("Type (Movie/TV Show): ");
        String type = scanner.nextLine().trim();

        if (!type.equals("Movie") && !type.equals("TV Show")) {
            throw new IllegalArgumentException("Type must be 'Movie' or 'TV Show'");
        }

        System.out.print("Genre: ");
        String genre = scanner.nextLine().trim();

        System.out.print("Year: ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year must be a valid number");
        }

        System.out.print("Creator (Director/Showrunner): ");
        String creator = scanner.nextLine().trim();

        Title title = new Title(name, type, genre, year, creator);
        title.save();
        System.out.println("Title added successfully!");
    }

    private static void addToWatchList() throws SQLException {
        System.out.print("Title name: ");
        String titleName = scanner.nextLine().trim();

        System.out.print("Status (Plan to Watch/Watching/Completed): ");
        String status = scanner.nextLine().trim();

        WatchList.addToWatchList(titleName, status);
        System.out.println("Added to watch list!");
    }

    private static void updateWatchStatus() throws SQLException {
        System.out.print("Title name: ");
        String titleName = scanner.nextLine().trim();

        System.out.print("New status (Plan to Watch/Watching/Completed): ");
        String newStatus = scanner.nextLine().trim();

        WatchList.updateStatusByTitleName(titleName, newStatus);
        System.out.println("Status updated!");
    }

    private static void addReview() throws SQLException {
        System.out.print("Title name: ");
        String titleName = scanner.nextLine().trim();

        System.out.print("Rating (1-5): ");
        int rating;
        try {
            rating = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Rating must be a number between 1 and 5");
        }

        System.out.print("Comment (optional, press Enter to skip): ");
        String comment = scanner.nextLine().trim();

        Review.insertReview(titleName, rating, comment.isEmpty() ? null : comment);
        System.out.println("Review added successfully!");
    }

    private static void viewAllTitles() throws SQLException {
        List<Title> titles = Title.getAllTitles();
        if (titles.isEmpty()) {
            System.out.println("No titles found.");
        } else {
            System.out.println("\n=== All Titles ===");
            for (Title title : titles) {
                System.out.println("- " + title.getName() + " (" + title.getType() + ", " + title.getYear() + ")");
                System.out.println("  Genre: " + title.getGenre() + " | Creator: " + title.getCreator());
            }
        }
    }

    private static void viewWatchList() throws SQLException {
        List<WatchList.WatchListEntry> entries = WatchList.getWatchList();
        if (entries.isEmpty()) {
            System.out.println("Your watch list is empty.");
        } else {
            System.out.println("\n=== Your Watch List ===");
            for (WatchList.WatchListEntry entry : entries) {
                System.out.println("- " + entry.toString());
            }
        }
    }

    private static void viewReviews() throws SQLException {
        System.out.print("View reviews for which title? (Leave blank to view all): ");
        String titleName = scanner.nextLine().trim();

        List<Review.ReviewEntry> reviews;
        if (titleName.isEmpty()) {
            reviews = Review.getAllReviews();
            if (reviews.isEmpty()) {
                System.out.println("No reviews found.");
            } else {
                System.out.println("\n=== All Reviews ===");
                for (Review.ReviewEntry review : reviews) {
                    System.out.println("- " + review.toString());
                }
            }
        } else {
            reviews = Review.getReviewsForTitle(titleName);
            if (reviews.isEmpty()) {
                System.out.println("No reviews found for this title.");
            } else {
                System.out.println("\n=== Reviews for " + titleName + " ===");
                for (Review.ReviewEntry review : reviews) {
                    System.out.println("- " + review.toString());
                }
            }
        }
    }
}
