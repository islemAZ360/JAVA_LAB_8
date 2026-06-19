package main.java.client.gui.layout;

import main.java.client.gui.components.avatar.UiAvatar;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.sidebar.UiSidebar;
import main.java.client.gui.core.Messages;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application shell (always visible after login).
 * Structure:
 * - Left: Sidebar with navigation menu
 * - Center: contentHost (where Pages are rendered)
 */
public class MainShell extends BorderPane {

    private final StackPane contentHost;
    private final UiSidebar sidebar;
    private final Stage stage;
    private final String currentUser;
    private Runnable onLogout;

    public MainShell(Stage stage, String currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;

        this.getStyleClass().add("app-shell");

        // Content host (center area)
        this.contentHost = new StackPane();
        this.contentHost.getStyleClass().add("app-content-host");

        // Build sidebar
        this.sidebar = buildSidebar();

        this.setLeft(sidebar);
        this.setCenter(contentHost);
    }

    public StackPane getContentHost() {
        return contentHost;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * Add a menu item to the sidebar.
     */
    public MainShell addItem(String label, Runnable action) {
        sidebar.addItem(label, action);
        return this;
    }

    /**
     * Set content of the contentHost area.
     */
    public void setContent(Node content) {
        contentHost.getChildren().setAll(content);
        StackPane.setAlignment(content, Pos.CENTER);
    }

    /**
     * Set logout callback (called when user clicks Logout button).
     */
    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    private UiSidebar buildSidebar() {
        // Footer: Avatar + Username + Logout button
        UiAvatar avatar = new UiAvatar(currentUser);

        Label userLabel = new Label(
                Messages.get(Messages.Key.CURRENT_USER) + ":\n" + currentUser
        );
        userLabel.getStyleClass().add("current-user-label");

        UiButton logout = new UiButton(
                Messages.get(Messages.Key.LOGOUT),
                ButtonVariant.OUTLINE
        );
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });

        VBox footer = new VBox(10, avatar, userLabel, logout);
        footer.setAlignment(Pos.CENTER_LEFT);

        return new UiSidebar("HumanBeing GUI").setFooter(footer);
    }
}
