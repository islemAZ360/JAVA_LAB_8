package main.java.client.gui.components.textarea;

import javafx.scene.control.TextArea;

/**
 * UiTextarea — styled multi-line text input (shadcn Textarea).
 *
 * Usage:
 *   UiTextarea ta = new UiTextarea("Write something...");
 *   ta.setPrefRowCount(4);
 */
public class UiTextarea extends TextArea {
    public UiTextarea() {
        getStyleClass().add("ui-textarea");
        setWrapText(true);
        setPrefRowCount(3);
    }
    public UiTextarea(String placeholder) {
        this();
        setPromptText(placeholder);
    }
}
