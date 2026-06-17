package client.gui.components.input;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

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

        if (prefix != null && !prefix.isBlank()) {
            Label prefixLabel = new Label(prefix);
            prefixLabel.getStyleClass().add("ui-input-prefix");
            getChildren().add(prefixLabel);
        }

        getChildren().add(input);

        if (suffix != null) {
            suffix.getStyleClass().add("ui-input-suffix");
            getChildren().add(suffix);
        }
    }

    public TextField input() {
        return input;
    }

    public String getText() {
        return input.getText();
    }

    public void setText(String value) {
        input.setText(value);
    }
}
