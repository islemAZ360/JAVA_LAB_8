package main.java.client.gui.components.label;

import javafx.scene.control.Label;

/**
 * UiLabel — styled form label (shadcn Label).
 *
 * Usage:
 *   new UiLabel("Email address")
 *   new UiLabel("Required *").setRequired(true)
 */
public class UiLabel extends Label {
    public UiLabel(String text) {
        super(text);
        getStyleClass().add("ui-label");
    }
    public UiLabel setRequired(boolean r) {
        String base = getText().replace(" *", "");
        setText(r ? base + " *" : base);
        return this;
    }
}
