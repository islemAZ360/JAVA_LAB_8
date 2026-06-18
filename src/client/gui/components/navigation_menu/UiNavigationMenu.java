package client.gui.components.navigation_menu;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import java.util.function.Consumer;

/**
 * UiNavigationMenu — horizontal top navigation (shadcn NavigationMenu).
 *
 * Usage:
 *   UiNavigationMenu nav = new UiNavigationMenu();
 *   nav.addItem("Home",     () -> goHome());
 *   nav.addItem("About",    () -> goAbout());
 *   nav.addItem("Contact",  () -> goContact());
 */
public class UiNavigationMenu extends HBox {
    private Consumer<String> onNavigate;

    public UiNavigationMenu() {
        getStyleClass().add("ui-nav-menu");
        setSpacing(4);
        setAlignment(Pos.CENTER_LEFT);
    }

    public UiNavigationMenu addItem(String label, Runnable action) {
        Hyperlink link = new Hyperlink(label);
        link.getStyleClass().add("ui-nav-item");
        link.setOnAction(e -> { if (action != null) action.run(); });
        getChildren().add(link);
        return this;
    }

    public UiNavigationMenu addItem(String label, Node icon, Runnable action) {
        Hyperlink link = new Hyperlink(label, icon);
        link.getStyleClass().add("ui-nav-item");
        link.setOnAction(e -> { if (action != null) action.run(); });
        getChildren().add(link);
        return this;
    }

    public void setOnNavigate(Consumer<String> cb) { this.onNavigate = cb; }
}
