package client.gui.core;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public final class OwnerColorPalette {
    private OwnerColorPalette() {}

    private static final Color[] PALETTE = {
            Color.web("#14b8a6"), // teal
            Color.web("#f97316"), // orange
            Color.web("#8b5cf6"), // violet
            Color.web("#eab308"), // yellow
            Color.web("#3b82f6"), // blue
            Color.web("#ef4444"), // red
            Color.web("#22c55e"), // green
            Color.web("#ec4899")  // pink
    };

    private static final Map<String, Color> CACHE = new HashMap<>();

    public static Color colorFor(String owner) {
        String key = owner == null || owner.isBlank() ? "unknown" : owner.trim();
        return CACHE.computeIfAbsent(key, OwnerColorPalette::stableColor);
    }

    public static Color textFor(Color background) {
        double luminance = 0.299 * background.getRed()
                + 0.587 * background.getGreen()
                + 0.114 * background.getBlue();
        return luminance > 0.55 ? Color.BLACK : Color.WHITE;
    }

    public static String toRgb(Color color) {
        return String.format("rgb(%d,%d,%d)", channel(color.getRed()), channel(color.getGreen()), channel(color.getBlue()));
    }

    public static String toRgba(Color color, double opacity) {
        double alpha = Math.max(0, Math.min(1, opacity));
        return String.format("rgba(%d,%d,%d,%.3f)", channel(color.getRed()), channel(color.getGreen()), channel(color.getBlue()), alpha);
    }

    private static Color stableColor(String owner) {
        int index = Math.floorMod(owner.hashCode(), PALETTE.length);
        return PALETTE[index];
    }

    private static int channel(double value) {
        return (int) Math.round(Math.max(0, Math.min(1, value)) * 255);
    }
}
