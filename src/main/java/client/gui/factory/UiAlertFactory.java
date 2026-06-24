package main.java.client.gui.factory;

import main.java.client.gui.components.alert.UiAlert;
import main.java.client.gui.components.alert.AlertVariant;

public class UiAlertFactory {

    public static UiAlert create(String rawJson) {
        String title = "System Error";
        String message = "Cannot parse UI data.";
        AlertVariant variant = AlertVariant.ERROR;

        try {
            String cleanJson = rawJson.trim();
            if (cleanJson.startsWith("{") && cleanJson.endsWith("}")) {
                cleanJson = cleanJson.substring(1, cleanJson.length() - 1);
            }

            String extractedTitle = getJsonValue(cleanJson, "title");
            String extractedMessage = getJsonValue(cleanJson, "message");
            String extractedVariant = getJsonValue(cleanJson, "variant");

            if (extractedVariant != null) {
                try {
                    variant = AlertVariant.valueOf(extractedVariant.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    variant = AlertVariant.INFO;
                }
            }

            if (extractedTitle != null) title = extractedTitle;
            if (extractedMessage != null) message = extractedMessage;

        } catch (Exception e) {
            System.err.println("[UiAlertFactory Pure Error]: " + e.getMessage());
        }

        return new UiAlert(title, message, variant);
    }


    private static String getJsonValue(String json, String key) {
        String targetKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(targetKey);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(":", keyIndex + targetKey.length());
        if (colonIndex == -1) return null;

        int openQuoteIndex = json.indexOf("\"", colonIndex);
        if (openQuoteIndex == -1) return null;

        int closeQuoteIndex = json.indexOf("\"", openQuoteIndex + 1);
        if (closeQuoteIndex == -1) return null;

        String value = json.substring(openQuoteIndex + 1, closeQuoteIndex);

        return value.replace("\\n", "\n").replace("\\\"", "\"");
    }
}
