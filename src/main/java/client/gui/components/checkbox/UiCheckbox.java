package main.java.client.gui.components.checkbox;

import javafx.scene.control.CheckBox;

/**
 * UiCheckbox — styled CheckBox (shadcn Checkbox).
 *
 * Usage:
 *   UiCheckbox cb = new UiCheckbox("Accept terms");
 *   cb.isSelected();
 */
public class UiCheckbox extends CheckBox {
    public UiCheckbox() { getStyleClass().add("ui-checkbox"); }
    public UiCheckbox(String text) { super(text); getStyleClass().add("ui-checkbox"); }
}
