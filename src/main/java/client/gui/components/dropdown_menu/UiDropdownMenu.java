package main.java.client.gui.components.dropdown_menu;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * UiDropdownMenu — dropdown triggered by a node (shadcn DropdownMenu).
 *
 * Usage:
 *   UiDropdownMenu menu = new UiDropdownMenu()
 *       .addItem("Edit",   () -> openEdit())
 *       .addItem("Delete", () -> doDelete())
 *       .addSeparator()
 *       .addItem("Settings", () -> openSettings());
 *   menu.attachTo(triggerBtn);
 */
public class UiDropdownMenu {
    private final ContextMenu ctx = new ContextMenu();

    public UiDropdownMenu() {
        ctx.getStyleClass().add("ui-dropdown-menu");
    }

    public UiDropdownMenu addItem(String label, Runnable action) {
        MenuItem item = new MenuItem(label);
        item.getStyleClass().add("ui-dropdown-item");
        item.setOnAction(e -> { if (action != null) action.run(); });
        ctx.getItems().add(item);
        return this;
    }

    public UiDropdownMenu addItem(String label, Node icon, Runnable action) {
        MenuItem item = new MenuItem(label, icon);
        item.getStyleClass().add("ui-dropdown-item");
        item.setOnAction(e -> { if (action != null) action.run(); });
        ctx.getItems().add(item);
        return this;
    }

    public UiDropdownMenu addSeparator() {
        ctx.getItems().add(new SeparatorMenuItem());
        return this;
    }

    public UiDropdownMenu addDisabled(String label) {
        MenuItem item = new MenuItem(label);
        item.setDisable(true);
        ctx.getItems().add(item);
        return this;
    }

    /** Toggle on click. */
    public void attachTo(Node trigger) {
        trigger.setOnMouseClicked(e -> {
            if (ctx.isShowing()) ctx.hide();
            else ctx.show(trigger, javafx.geometry.Side.BOTTOM, 0, 4);
        });
    }

    public ContextMenu getContextMenu() { return ctx; }
}
