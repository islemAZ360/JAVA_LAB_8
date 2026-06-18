package client.gui.components.button_group;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 * UiButtonGroup — horizontal group of buttons sharing borders.
 * Matches shadcn/ui ButtonGroup.
 *
 * Usage:
 *   new UiButtonGroup(btn1, btn2, btn3)
 */
public class UiButtonGroup extends HBox {
    public UiButtonGroup(Node... buttons) {
        getStyleClass().add("ui-button-group");
        setSpacing(0);
        if (buttons != null) {
            for (int i = 0; i < buttons.length; i++) {
                Node btn = buttons[i];
                btn.getStyleClass().add("ui-button-group-item");
                if (i == 0) btn.getStyleClass().add("ui-button-group-first");
                else if (i == buttons.length - 1) btn.getStyleClass().add("ui-button-group-last");
                else btn.getStyleClass().add("ui-button-group-middle");
                getChildren().add(btn);
            }
        }
    }

    public UiButtonGroup add(Node btn) {
        // Remove last marker from old last
        if (!getChildren().isEmpty()) {
            Node old = getChildren().getLast();
            old.getStyleClass().remove("ui-button-group-last");
            old.getStyleClass().add("ui-button-group-middle");
        }
        btn.getStyleClass().addAll("ui-button-group-item", "ui-button-group-last");
        getChildren().add(btn);
        return this;
    }
}
