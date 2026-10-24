package com.watchcubed;

public class HypeFormatter extends Formatter {
    @Override
    public String formatSuccess(String message) {
        String loud = "\u001B[1m" + message.toUpperCase() + "!!!";
        return colorText(loud, GREEN);
    }

    @Override
    public String formatError(String message) {
        String loud = "\u0007\u001B[1m" + message.toUpperCase() + "!!!";
        return colorText(loud, RED);
    }

    @Override
    public String formatInfo(String message) {
        String loud = "\u0007\u001B[1m" + message.toUpperCase() + "!!!";
        return colorText(loud, BLUE);
    }
}
