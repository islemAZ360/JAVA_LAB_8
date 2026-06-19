package main.java.client.gui.components.badge;

import javafx.scene.control.Label;
import java.util.Arrays;

/**
 * UiBadge — inline badge/chip (shadcn Badge).
 *
 * Usage:
 *   new UiBadge("New")
 *   new UiBadge("Error", BadgeVariant.DESTRUCTIVE)
 */
public class UiBadge extends Label {
    public UiBadge(String text) {
        super(text);
        getStyleClass().add("ui-badge");
        applyVariant(BadgeVariant.DEFAULT);
    }

    public UiBadge(String text, BadgeVariant variant) {
        this(text);
        applyVariant(variant);
    }

    public UiBadge applyVariant(BadgeVariant v) {
        getStyleClass().removeAll(Arrays.stream(BadgeVariant.values()).map(BadgeVariant::cssClass).toList());
        getStyleClass().add((v == null ? BadgeVariant.DEFAULT : v).cssClass());
        return this;
    }
}
