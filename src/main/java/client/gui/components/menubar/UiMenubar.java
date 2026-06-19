package main.java.client.gui.components.menubar;

import javafx.scene.control.*;

/**
 * UiMenubar — application menu bar (shadcn Menubar).
 *
 * Usage:
 *   UiMenubar bar = new UiMenubar();
 *   bar.addMenu("File")
 *       .addItem("New",  "Ctrl+N", () -> newFile())
 *       .addItem("Open", "Ctrl+O", () -> openFile())
 *       .addSeparator()
 *       .addItem("Exit", null, () -> exit());
 */
public class UiMenubar extends MenuBar {

    public UiMenubar() {
        getStyleClass().add("ui-menubar");
    }

    public MenuBuilder addMenu(String title) {
        Menu menu = new Menu(title);
        menu.getStyleClass().add("ui-menubar-menu");
        getMenus().add(menu);
        return new MenuBuilder(menu);
    }

    public static class MenuBuilder {
        private final Menu menu;
        MenuBuilder(Menu m) { this.menu = m; }

        public MenuBuilder addItem(String label, String shortcut, Runnable action) {
            MenuItem item = new MenuItem(label);
            item.getStyleClass().add("ui-menubar-item");
            if (shortcut != null) item.setAccelerator(javafx.scene.input.KeyCombination.keyCombination(shortcut));
            item.setOnAction(e -> { if (action != null) action.run(); });
            menu.getItems().add(item);
            return this;
        }

        public MenuBuilder addSeparator() {
            menu.getItems().add(new SeparatorMenuItem());
            return this;
        }

        public MenuBuilder addCheckItem(String label, boolean selected, java.util.function.Consumer<Boolean> onChange) {
            CheckMenuItem item = new CheckMenuItem(label);
            item.setSelected(selected);
            item.selectedProperty().addListener((obs, o, n) -> { if (onChange != null) onChange.accept(n); });
            menu.getItems().add(item);
            return this;
        }
    }
}
