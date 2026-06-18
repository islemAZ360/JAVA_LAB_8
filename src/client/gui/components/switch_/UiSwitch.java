package client.gui.components.switch_;

import javafx.scene.control.ToggleButton;

/**
 * UiSwitch — toggle switch (shadcn Switch).
 *
 * Usage:
 *   UiSwitch sw = new UiSwitch("Dark mode");
 *   sw.selectedProperty().addListener((obs, o, n) -> applyTheme(n));
 */
public class UiSwitch extends ToggleButton {
    public UiSwitch() {
        getStyleClass().add("ui-switch");
        selectedProperty().addListener((obs, o, n) -> {
            if (n) getStyleClass().add("ui-switch-on");
            else getStyleClass().remove("ui-switch-on");
        });
    }
    public UiSwitch(String label) { this(); setText(label); }
    public boolean isOn() { return isSelected(); }
}
