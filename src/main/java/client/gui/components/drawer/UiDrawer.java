package main.java.client.gui.components.drawer;

import javafx.geometry.Side;
import javafx.stage.Window;
import main.java.client.gui.components.sheet.UiSheet;

/**
 * UiDrawer — alias for Sheet with drawer semantics (shadcn Drawer).
 * Slides from BOTTOM by default like a mobile drawer.
 *
 * Usage:
 *   UiDrawer d = new UiDrawer("Options", owner);
 *   d.content().getChildren().add(actionsBox);
 *   d.show();
 */
public class UiDrawer extends UiSheet {
    public UiDrawer(String title, Window owner) {
        super(title, Side.BOTTOM, owner);
    }
    public UiDrawer(String title, Side side, Window owner) {
        super(title, side, owner);
    }
}
