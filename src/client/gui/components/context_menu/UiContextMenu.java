package client.gui.components.context_menu;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * UiContextMenu — right-click context menu (shadcn ContextMenu).
 *
 * Usage:
 *   UiContextMenu menu = new UiContextMenu()
 *       .addItem("Copy",  () -> copy())
 *       .addItem("Paste", () -> paste())
 *       .addSeparator()
 *       .addItem("Delete", () -> delete());
 *   menu.attachTo(targetNode);
 */
public class UiContextMenu {
    private final ContextMenu ctx = new ContextMenu();

    public UiContextMenu() {
        ctx.getStyleClass().add("ui-context-menu");
    }

    public UiContextMenu addItem(String label, Runnable action) {
        MenuItem item = new MenuItem(label);
        item.getStyleClass().add("ui-context-item");
        item.setOnAction(e -> { if (action != null) action.run(); });
        ctx.getItems().add(item);
        return this;
    }

    public UiContextMenu addSeparator() {
        ctx.getItems().add(new SeparatorMenuItem());
        return this;
    }

    public void attachTo(Node node) {
        node.setOnContextMenuRequested(e -> ctx.show(node, e.getScreenX(), e.getScreenY()));
    }
}
