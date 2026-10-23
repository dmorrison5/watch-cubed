package com.watchcubed;

public abstract class Formatter {
    protected static final String RESET = "\u001B[0m";
    protected static final String RED = "\u001B[31m";
    protected static final String GREEN = "\u001B[32m";
    protected static final String BLUE = "\u001B[34m";
    protected static final String CYAN = "\u001B[36m";

    public static String colorText(String text, String color) {
        return color + text + RESET;
    }

    public abstract String formatSuccess(String message);

    public abstract String formatError(String message);

    public abstract String formatInfo(String message);
}
