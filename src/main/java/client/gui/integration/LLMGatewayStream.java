package main.java.client.gui.integration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LLMGatewayStream {

    private static final Logger logger = Logger.getLogger(LLMGatewayStream.class.getName());

    private static final String STREAM_URL = "http://127.0.0.1:5173/api/stream";
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 60_000;
    private static final String DONE_SIGNAL = "[DONE]";

    public void sendQuery(String query, Consumer<String> onToken, Consumer<String> onComplete, Consumer<String> onError) {
        HttpURLConnection conn = null;
        try {
            URL url = URI.create(STREAM_URL).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setRequestProperty("Cache-Control", "no-cache");

            String body = "{\"query\":\"" + escapeJson(query) + "\"}";
            conn.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            conn.getOutputStream().flush();

            int status = conn.getResponseCode();
            if (status != 200) {
                onError.accept("Server returned HTTP " + status);
                return;
            }

            StringBuilder fullResponse = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {

                    if (!line.startsWith("data:")) continue;

                    String payload = line.substring(5).trim(); // strip "data: "

                    if (DONE_SIGNAL.equals(payload)) break;

                    String token = extractToken(payload);
                    if (token.isEmpty()) continue;

                    fullResponse.append(token);

                    if (onToken != null) {
                        onToken.accept(token);
                    }
                }
            }

            String json = sanitizeResponse(fullResponse.toString());
            onComplete.accept(json);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Stream error", e);
            onError.accept("Stream failed: " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private String extractToken(String payload) {
        if (payload.startsWith("{") && payload.contains("\"token\"")) {
            // Format A: {"token": "..."}
            int start = payload.indexOf("\"token\"");
            int colon = payload.indexOf(":", start);
            int q1 = payload.indexOf("\"", colon + 1);
            if (q1 == -1) return "";
            int q2 = q1 + 1;
            StringBuilder sb = new StringBuilder();
            while (q2 < payload.length()) {
                char c = payload.charAt(q2);
                if (c == '\\' && q2 + 1 < payload.length()) {
                    char next = payload.charAt(q2 + 1);
                    sb.append(switch (next) {
                        case '"' -> '"';
                        case '\\' -> '\\';
                        case 'n' -> '\n';
                        case 'r' -> '\r';
                        case 't' -> '\t';
                        default -> next;
                    });
                    q2 += 2;
                } else if (c == '"') {
                    break;
                } else {
                    sb.append(c);
                    q2++;
                }
            }
            return sb.toString();
        }

        return payload;
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
