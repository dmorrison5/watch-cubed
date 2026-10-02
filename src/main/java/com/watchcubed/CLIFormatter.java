package com.watchcubed;

import java.util.List;

public class CLIFormatter {
    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final int MAX_COL_WIDTH = 30;

    /**
     * Clears the console screen
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Prints a formatted header
     */
    public static void printHeader(String title) {
        System.out.println("\n" + colorText("=== " + title + " ===", CYAN));
    }

    /**
     * Prints a numbered menu from an array of options
     */
    public static void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.print("Choose an option: ");
    }

    /**
     * Returns text colored with ANSI escape codes
     */
    public static String colorText(String text, String color) {
        return color + text + RESET;
    }

    /**
     * Prints an error message in red
     */
    public static void printError(String message) {
        System.out.println(colorText("Error: " + message, RED));
    }

    /**
     * Prints a success message in green
     */
    public static void printSuccess(String message) {
        System.out.println(colorText(message, GREEN));
    }

    /**
     * Prints an info message in blue
     */
    public static void printInfo(String message) {
        System.out.println(colorText(message, BLUE));
    }

    /**
     * Prints a formatted table with headers and rows
     * Columns are aligned and cells are truncated to fit column width
     */
    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) {
            return;
        }

        // Treat null rows as empty
        if (rows == null) {
            rows = new java.util.ArrayList<>();
        }

        // Calculate column widths
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }

        for (String[] row : rows) {
            for (int i = 0; i < row.length && i < columnWidths.length; i++) {
                String cell = (row[i] == null) ? "" : row[i];
                columnWidths[i] = Math.max(columnWidths[i], cell.length());
            }
        }

        // Cap column widths at MAX_COL_WIDTH
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = Math.min(columnWidths[i], MAX_COL_WIDTH);
        }

        // Print header
        printTableRow(headers, columnWidths, true);

        // Print separator
        printTableSeparator(columnWidths);

        // Print rows
        for (String[] row : rows) {
            printTableRow(row, columnWidths, false);
        }
        System.out.println();
    }

    /**
     * Helper to print a single table row with proper alignment
     */
    private static void printTableRow(String[] cells, int[] columnWidths, boolean isHeader) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnWidths.length; i++) {
            String cell = (i < cells.length && cells[i] != null) ? cells[i] : "";
            // Truncate cell if it's longer than column width
            if (cell.length() > columnWidths[i]) {
                if (columnWidths[i] <= 3) {
                    cell = cell.substring(0, columnWidths[i]);
                } else {
                    cell = cell.substring(0, columnWidths[i] - 3) + "...";
                }
            }
            // Left-align and pad with spaces
            sb.append(String.format("%-" + columnWidths[i] + "s", cell));
            if (i < columnWidths.length - 1) {
                sb.append(" | ");
            }
        }

        String output = sb.toString();
        if (isHeader) {
            System.out.println(colorText(output, CYAN));
        } else {
            System.out.println(output);
        }
    }

    /**
     * Helper to print a table separator line
     */
    private static void printTableSeparator(int[] columnWidths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnWidths.length; i++) {
            for (int j = 0; j < columnWidths[i]; j++) {
                sb.append("-");
            }
            if (i < columnWidths.length - 1) {
                sb.append("-+-");
            }
        }
        System.out.println(colorText(sb.toString(), CYAN));
    }
}
