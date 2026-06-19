package main.java.client.gui.components.input_group;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * UiInputGroup — input with optional prefix/suffix (shadcn InputGroup).
 *
 * Usage:
 *   new UiInputGroup("Search...")
 *   new UiInputGroup("@", "username", null)
 *   new UiInputGroup(null, "Amount", dollarLabel)
 */
public class UiInputGroup extends HBox {
    private final TextField input = new TextField();

    public UiInputGroup(String placeholder) {
        this(null, placeholder, null);
    }

    public UiInputGroup(String prefix, String placeholder, Node suffix) {
        getStyleClass().add("ui-input-group");
        setSpacing(0);
        input.getStyleClass().add("ui-input");
        input.setPromptText(placeholder == null ? "" : placeholder);
        HBox.setHgrow(input, Priority.ALWAYS);

        if (prefix != null && !prefix.isBlank()) {
            Label pre = new Label(prefix);
            pre.getStyleClass().add("ui-input-prefix");
            getChildren().add(pre);
        }
        getChildren().add(input);
        if (suffix != null) {
            suffix.getStyleClass().add("ui-input-suffix");
            getChildren().add(suffix);
        }
    }

    public TextField input() { return input; }
    public String getText() { return input.getText(); }
    public void setText(String v) { input.setText(v); }
    public void clear() { input.clear(); }
}
