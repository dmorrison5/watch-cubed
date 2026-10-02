package com.watchcubed;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static boolean running = true;

    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeTables();
            showMenu();
        } catch (SQLException e) {
            CLIFormatter.printError("Database error: " + e.getMessage());
        } finally {
            try {
                DatabaseConnection.closeConnection();
            } catch (SQLException e) {
                CLIFormatter.printError("Error closing database connection: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {
        while (running) {
            CLIFormatter.printHeader("Watch Cubed");

            String[] menuOptions = {
                "Add title",
                "Add to watch list",
                "Update watch status",
                "Add review",
                "View all titles",
                "View watch list",
                "View reviews",
                "Edit title",
                "Delete title",
                "Exit"
            };

            CLIFormatter.printMenu(menuOptions);

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
                        editTitle();
                        break;
                    case "9":
                        deleteTitle();
                        break;
                    case "10":
                        running = false;
                        CLIFormatter.printSuccess("Goodbye!");
                        break;
                    default:
                        CLIFormatter.printError("Invalid option. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                CLIFormatter.printError(e.getMessage());
            } catch (SQLException e) {
                CLIFormatter.printError("Database error: " + e.getMessage());
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
        CLIFormatter.printSuccess("Title added successfully!");
    }

    private static void addToWatchList() throws SQLException {
        System.out.print("Title name: ");
        String titleName = scanner.nextLine().trim();

        System.out.print("Status (Plan to Watch/Watching/Completed): ");
        String status = scanner.nextLine().trim();

        WatchList.addToWatchList(titleName, status);
        CLIFormatter.printSuccess("Added to watch list!");
    }

    private static void updateWatchStatus() throws SQLException {
        System.out.print("Title name: ");
        String titleName = scanner.nextLine().trim();

        System.out.print("New status (Plan to Watch/Watching/Completed): ");
        String newStatus = scanner.nextLine().trim();

        WatchList.updateStatusByTitleName(titleName, newStatus);
        CLIFormatter.printSuccess("Status updated!");
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
        CLIFormatter.printSuccess("Review added successfully!");
    }

    private static void viewAllTitles() throws SQLException {
        List<Title> titles = Title.getAllTitles();
        if (titles.isEmpty()) {
            CLIFormatter.printInfo("No titles found.");
            return;
        }

        String[] headers = {"Name", "Type", "Year", "Genre", "Creator"};
        List<String[]> rows = new ArrayList<>();

        for (Title title : titles) {
            rows.add(new String[] {
                title.getName(),
                title.getType(),
                String.valueOf(title.getYear()),
                title.getGenre(),
                title.getCreator()
            });
        }

        CLIFormatter.printHeader("All Titles");
        CLIFormatter.printTable(headers, rows);
    }

    private static void viewWatchList() throws SQLException {
        List<WatchList.WatchListEntry> entries = WatchList.getWatchList();
        if (entries.isEmpty()) {
            CLIFormatter.printInfo("Your watch list is empty.");
            return;
        }

        String[] headers = {"Title", "Type", "Year", "Status"};
        List<String[]> rows = new ArrayList<>();

        for (WatchList.WatchListEntry entry : entries) {
            rows.add(new String[] {
                entry.getTitleName(),
                entry.getType(),
                String.valueOf(entry.getYear()),
                entry.getStatus()
            });
        }

        CLIFormatter.printHeader("Your Watch List");
        CLIFormatter.printTable(headers, rows);
    }

    private static void viewReviews() throws SQLException {
        System.out.print("View reviews for which title? (Leave blank to view all): ");
        String titleName = scanner.nextLine().trim();

        List<Review.ReviewEntry> reviews;
        if (titleName.isEmpty()) {
            reviews = Review.getAllReviews();
            if (reviews.isEmpty()) {
                CLIFormatter.printInfo("No reviews found.");
                return;
            }
            CLIFormatter.printHeader("All Reviews");
        } else {
            reviews = Review.getReviewsForTitle(titleName);
            if (reviews.isEmpty()) {
                CLIFormatter.printInfo("No reviews found for this title.");
                return;
            }
            CLIFormatter.printHeader("Reviews for " + titleName);
        }

        String[] headers = {"Title", "Type", "Year", "Rating", "Comment"};
        List<String[]> rows = new ArrayList<>();

        for (Review.ReviewEntry review : reviews) {
            rows.add(new String[] {
                review.getTitleName(),
                review.getType(),
                String.valueOf(review.getYear()),
                String.valueOf(review.getRating()),
                review.getComment() == null ? "" : review.getComment()
            });
        }

        CLIFormatter.printTable(headers, rows);
    }

    private static void editTitle() throws SQLException {
        System.out.print("Title name: ");
        String titleName = scanner.nextLine().trim();

        Title title = Title.getByName(titleName);
        if (title == null) {
            throw new IllegalArgumentException("Title not found: " + titleName);
        }

        System.out.println("Current title: " + title.getName());
        System.out.print("New name (press Enter to keep '" + title.getName() + "'): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            title.setName(newName);
        }

        System.out.println("Current type: " + title.getType());
        System.out.print("New type (press Enter to keep '" + title.getType() + "'): ");
        String newType = scanner.nextLine().trim();
        if (!newType.isEmpty()) {
            if (!newType.equals("Movie") && !newType.equals("TV Show")) {
                throw new IllegalArgumentException("Type must be exactly 'Movie' or 'TV Show'");
            }
            title.setType(newType);
        }

        System.out.println("Current genre: " + title.getGenre());
        System.out.print("New genre (press Enter to keep '" + title.getGenre() + "'): ");
        String newGenre = scanner.nextLine().trim();
        if (!newGenre.isEmpty()) {
            title.setGenre(newGenre);
        }

        System.out.println("Current year: " + title.getYear());
        System.out.print("New year (press Enter to keep '" + title.getYear() + "'): ");
        String newYearStr = scanner.nextLine().trim();
        if (!newYearStr.isEmpty()) {
            try {
                int newYear = Integer.parseInt(newYearStr);
                title.setYear(newYear);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Year must be a valid number");
            }
        }

        System.out.println("Current creator: " + title.getCreator());
        System.out.print("New creator (press Enter to keep '" + title.getCreator() + "'): ");
        String newCreator = scanner.nextLine().trim();
        if (!newCreator.isEmpty()) {
            title.setCreator(newCreator);
        }

        try {
            title.update();
            CLIFormatter.printSuccess("Title updated successfully!");
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new IllegalArgumentException("A title with this name already exists.");
            }
            throw e;
        }
    }

    private static void deleteTitle() throws SQLException {
        System.out.print("Title name: ");
        String titleName = scanner.nextLine().trim();

        Title title = Title.getByName(titleName);
        if (title == null) {
            throw new IllegalArgumentException("Title not found: " + titleName);
        }

        System.out.println("WARNING: Deleting this title will also delete its watch list entry and all reviews.");
        System.out.print("Are you sure you want to delete '" + title.getName() + "'? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("y")) {
            CLIFormatter.printInfo("Delete cancelled.");
            return;
        }

        title.delete();
        CLIFormatter.printSuccess("Title deleted successfully!");
    }
}
