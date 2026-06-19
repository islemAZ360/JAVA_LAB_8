package main.java.client.gui.components.kbd;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * UiKbd — keyboard shortcut display (shadcn Kbd).
 *
 * Usage:
 *   new UiKbd("⌘K")
 *   UiKbd.combo("Ctrl", "K")    // renders ⌘ + K
 */
public class UiKbd extends Label {
    public UiKbd(String key) {
        super(key);
        getStyleClass().add("ui-kbd");
    }

    public static HBox combo(String... keys) {
        HBox box = new HBox(4);
        box.getStyleClass().add("ui-kbd-combo");
        for (String k : keys) box.getChildren().add(new UiKbd(k));
        return box;
    }
}
