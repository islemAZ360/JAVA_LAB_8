package main.java.client.gui.components.field;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class UiField extends VBox {
    private final Label label = new Label();
    private final Label helper = new Label();
    private final Label error = new Label();
    private Node control;

    public UiField(String labelText, Node control) {
        getStyleClass().add("ui-field");
        setSpacing(5);
        label.getStyleClass().add("ui-field-label");
        helper.getStyleClass().add("ui-field-helper");
        error.getStyleClass().add("ui-field-error");

        this.label.setText(labelText == null ? "" : labelText);
        this.control = control;
        getChildren().addAll(label, control, helper, error);
        setError(null);
        setHelper(null);
    }

    public UiField setHelper(String text) {
        helper.setText(text == null ? "" : text);
        helper.setVisible(text != null && !text.isBlank());
        helper.setManaged(helper.isVisible());
        return this;
    }

    public UiField setError(String text) {
        error.setText(text == null ? "" : text);
        error.setVisible(text != null && !text.isBlank());
        error.setManaged(error.isVisible());
        pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("invalid"), error.isVisible());
        return this;
    }

    public Node control() {
        return control;
    }
}
