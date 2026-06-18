package client.gui.components.tooltip;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * UiTooltip — styled tooltip (shadcn Tooltip).
 *
 * Usage:
 *   UiTooltip.attach(myNode, "Click to save");
 *   UiTooltip.attach(myNode, "Shortcut: Ctrl+S", Duration.millis(200));
 */
public class UiTooltip {
    private UiTooltip() {}

    public static Tooltip attach(Node node, String text) {
        return attach(node, text, Duration.millis(500));
    }

    public static Tooltip attach(Node node, String text, Duration delay) {
        Tooltip tip = new Tooltip(text);
        tip.getStyleClass().add("ui-tooltip");
        tip.setShowDelay(delay);
        tip.setHideDelay(Duration.millis(200));
        tip.setWrapText(true);
        Tooltip.install(node, tip);
        return tip;
    }
}
