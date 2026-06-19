package main.java.client.gui.components.separator;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;

/**
 * UiSeparator — visual divider line (shadcn Separator).
 *
 * Usage:
 *   new UiSeparator()                          // horizontal
 *   new UiSeparator(Orientation.VERTICAL)      // vertical
 */
public class UiSeparator extends Separator {
    public UiSeparator() {
        getStyleClass().add("ui-separator");
    }
    public UiSeparator(Orientation orientation) {
        super(orientation);
        getStyleClass().add("ui-separator");
    }
}
