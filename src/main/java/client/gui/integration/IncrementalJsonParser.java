package main.java.client.gui.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IncrementalJsonParser {

    private Consumer<String> onTypeDetected;


    private BiConsumer<String, String> onItem;


    private Consumer<String> onComplete;


    private Consumer<String> onError;


    private final StringBuilder fullResponse = new StringBuilder();
    private String detectedType = null;


    private String currentArray = null;

    private final StringBuilder itemBuffer = new StringBuilder();
    private int itemDepth = 0;
    private boolean inItem = false;
    private boolean inString = false;
    private boolean escaped = false;


    public IncrementalJsonParser onTypeDetected(Consumer<String> cb) {
        this.onTypeDetected = cb;
        return this;
    }

    public IncrementalJsonParser onItem(BiConsumer<String, String> cb) {
        this.onItem = cb;
        return this;
    }

    public IncrementalJsonParser onComplete(Consumer<String> cb) {
        this.onComplete = cb;
        return this;
    }

    public IncrementalJsonParser onError(Consumer<String> errorCb) {
        this.onError = errorCb;
        return this;
    }


    public void feed(String token) {
        fullResponse.append(token);

        if (detectedType == null && fullResponse.length() >= 16) {
            detectedType = extractTypeEarly(fullResponse.toString());
            if (detectedType != null && onTypeDetected != null) {
                onTypeDetected.accept(detectedType);
            }
        }

        if (detectedType != null) {
            for (char c : token.toCharArray()) {
                parseChar(c);
            }
        }
    }

    public void complete(String fullJson) {
        if (onComplete != null) {
            onComplete.accept(fullJson);
        }
    }


    public void error(String message) {
        if (onError != null) {
            onError.accept(message);
        }
    }


    private void parseChar(char c) {

        if (escaped) {
            escaped = false;
            if (inItem) itemBuffer.append(c);
            return;
        }
        if (c == '\\' && inString) {
            escaped = true;
            if (inItem) itemBuffer.append(c);
            return;
        }
        if (c == '"') {
            inString = !inString;
            if (inItem) itemBuffer.append(c);
            return;
        }
        if (inString) {
            if (inItem) itemBuffer.append(c);
            return;
        }

        if (c == '[' && !inItem) {
            currentArray = detectArrayContext(fullResponse.toString());
        }
        if (c == ']' && !inItem) {
            currentArray = null;
        }

        if (c == '{') {
            if (currentArray != null && !inItem) {
                inItem = true;
                itemDepth = 1;
                itemBuffer.setLength(0);
                itemBuffer.append(c);
            } else if (inItem) {
                itemDepth++;
                itemBuffer.append(c);
            }
            return;
        }

        if (c == '}') {
            if (inItem) {
                itemDepth--;
                itemBuffer.append(c);
                if (itemDepth == 0) {
                    String itemJson = itemBuffer.toString();
                    String category = arrayToCategory(currentArray);
                    if (onItem != null && category != null) {
                        onItem.accept(category, itemJson);
                    }
                    inItem = false;
                    itemBuffer.setLength(0);
                }
            }
            return;
        }

        if (inItem) {
            itemBuffer.append(c);
        }
    }


    private String detectArrayContext(String so_far) {
        int len = so_far.length();
        String tail = so_far.substring(Math.max(0, len - 40));

        if (tail.contains("\"columns\"")) return "columns";
        if (tail.contains("\"rows\"")) return "rows";
        if (tail.contains("\"tabs\"")) return "tabs";
        if (tail.contains("\"fields\"")) return "fields";
        if (tail.contains("\"lines\"")) return "lines";
        if (tail.contains("\"options\"")) return "options";

        return null;
    }

    private String arrayToCategory(String arrayKey) {
        if (arrayKey == null) return null;
        return switch (arrayKey) {
            case "columns" -> "column";
            case "rows" -> "row";
            case "tabs" -> "tab";
            case "fields" -> "field";
            case "lines" -> "line";
            case "options" -> "option";
            default -> null;
        };
    }


    private String extractTypeEarly(String partial) {
        int typeIdx = partial.indexOf("\"type\"");
        if (typeIdx == -1) return null;
        int colon = partial.indexOf(":", typeIdx);
        if (colon == -1) return null;
        int q1 = partial.indexOf("\"", colon + 1);
        if (q1 == -1) return null;
        int q2 = partial.indexOf("\"", q1 + 1);
        if (q2 == -1) return null;
        return partial.substring(q1 + 1, q2).toLowerCase();
    }
}
