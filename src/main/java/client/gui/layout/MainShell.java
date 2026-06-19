package main.java.client.gui.layout;

import javafx.scene.layout.Region;
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

 * MainShell — application shell after login.
 *
 * Actual layout:
 *
 *   BorderPane (MainShell)
 *   ├── LEFT:   UiSidebar
 *   └── CENTER: centerStack  (StackPane, fills completely)
 *                 ├── contentHost  (fills fully — setAlignment CENTER + maxSize MAX)
 *                 └── terminal     (bottom overlay — Pos.BOTTOM_CENTER)
 *
 * contentHost fills the entire centerStack by:
 *   - StackPane.setAlignment(contentHost, Pos.TOP_LEFT)
 *   - contentHost.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
 * → contentHost always occupies 100% width/height of centerStack
 * → terminal floats as an overlay on top, without forcing the content to shrink
 */
public class MainShell extends BorderPane {

    private final StackPane       contentHost;
    private final UiSidebar       sidebar;
    private final Stage           stage;
    private final String          currentUser;
    private Runnable onLogout;

    public MainShell(Stage stage, String currentUser) {
        this.stage       = stage;
        this.currentUser = currentUser;

        getStyleClass().add("app-shell");

        // ── Content host ─────────────────────────────────────────────────────
        contentHost = new StackPane();
        contentHost.getStyleClass().add("app-content-host");

        // CRITICAL: must set maxSize to MAX to fully fill the parent StackPane
        contentHost.setMaxWidth(Double.MAX_VALUE);
        contentHost.setMaxHeight(Double.MAX_VALUE);
        contentHost.setMinWidth(100);
        contentHost.setMinHeight(100);

        // contentHost fills the entire centerStack
        StackPane.setAlignment(contentHost, Pos.TOP_LEFT);

        // ── Sidebar ──────────────────────────────────────────────────────────
        sidebar = buildSidebar();

        setLeft(sidebar);
        setCenter(contentHost);
    }

    // ── Public API ───────────────────────────────────────────────────────────

    public MainShell addItem(String label, Runnable action) {
        sidebar.addItem(label, action);
        return this;
    }

    public void setContent(Node content) {
        contentHost.getChildren().setAll(content);
        StackPane.setAlignment(content, Pos.TOP_LEFT);
        // Ensure the content also completely fills the contentHost
        if (content instanceof Region r) {
            r.setMaxWidth(Double.MAX_VALUE);
            r.setMaxHeight(Double.MAX_VALUE);
        }
    }

    public void setOnLogout(Runnable callback) { this.onLogout = callback; }

    public String getCurrentUser()        { return currentUser;  }

    // ── Sidebar ──────────────────────────────────────────────────────────────

    private UiSidebar buildSidebar() {
        UiAvatar avatar   = new UiAvatar(currentUser);
        Label userLabel   = new Label(
                Messages.get(Messages.Key.CURRENT_USER) + ":\n" + currentUser);
        userLabel.getStyleClass().add("current-user-label");

        UiButton logoutBtn = new UiButton(
                Messages.get(Messages.Key.LOGOUT), ButtonVariant.OUTLINE);
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> { if (onLogout != null) onLogout.run(); });

        VBox footer = new VBox(10, avatar, userLabel, logoutBtn);
        footer.setAlignment(Pos.CENTER_LEFT);

        return new UiSidebar("HumanBeing GUI").setFooter(footer);
    }
}
