package main.java.client.gui.integration;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LLMGateWay {
    private static final Logger logger = Logger.getLogger(LLMGateWay.class.getName());

    private static final String SERVER_URL = "http://127.0.0.1:5173/api/search";
    private final HttpClient httpClient;

    public LLMGateWay() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public String sendQuery(String query) throws Exception {
        String safeQuery = escapeJson(query);
        String jsonPayload = "{\"query\":\"" + safeQuery + "\"}";

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(SERVER_URL)).version(HttpClient.Version.HTTP_1_1).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200) {
                return sanitizeResponse(response.body());
            } else {
                logger.log(Level.WARNING, "Server returned non-200 status: {0}", response.statusCode());
                return buildAlert("Server Error", "Server returned HTTP code: " + response.statusCode(), "ERROR");
            }
        } catch (Exception e) {
            return buildAlert("Connection Error", "Cannot reach non stream gateway. Error: " + e.getMessage(), "ERROR");
        }
    }

    private String sanitizeResponse(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            return buildAlert("Empty Response", "The server returned an empty response.", "WARNING");
        }

        String cleaned = rawBody.trim().replaceAll("(?s)^```json\\s*", "").replaceAll("(?s)^```\\s*", "").replaceAll("```\\s*$", "").trim();

        if (cleaned.startsWith("{") && cleaned.endsWith("}")) {
            if (cleaned.contains("\"type\"")) {
                return cleaned;
            }
        }

        // Fallback: something unexpected came back — wrap it in an alert
        logger.log(Level.WARNING, "Unexpected response format, wrapping in alert: {0}", cleaned);
        return buildAlert("AI Response", stripMarkdown(cleaned), "INFO");
    }


    private String buildAlert(String title, String message, String variant) {
        return String.format("{\"type\":\"alert\",\"props\":{\"title\":\"%s\",\"message\":\"%s\",\"variant\":\"%s\"}}", escapeJson(title), escapeJson(message), escapeJson(variant));
    }

    private String stripMarkdown(String text) {
        return text.replaceAll("```[\\s\\S]*?```", "").replaceAll("`([^`]*)`", "$1").replaceAll("(?m)^#{1,6}\\s*", "").replaceAll("\\*\\*([^*]*)\\*\\*", "$1").replaceAll("\\*([^*]*)\\*", "$1").replaceAll("(?m)^[*-]\\s+", "• ").replaceAll("\\n{3,}", "\n\n").trim();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
