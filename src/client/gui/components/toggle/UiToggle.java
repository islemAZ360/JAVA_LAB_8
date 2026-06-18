package client.gui.components.toggle;

import javafx.scene.control.ToggleButton;

/**
 * UiToggle — single toggle button (shadcn Toggle).
 *
 * Usage:
 *   UiToggle t = new UiToggle("Bold");
 *   t.selectedProperty().addListener(...);
 */
public class UiToggle extends ToggleButton {
    public UiToggle(String text) {
        super(text);
        getStyleClass().add("ui-toggle");
        selectedProperty().addListener((obs, o, n) -> {
            if (n) getStyleClass().add("ui-toggle-active");
            else getStyleClass().remove("ui-toggle-active");
        });
    }
}
