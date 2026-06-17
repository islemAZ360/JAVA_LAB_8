package client.gui.mockup;

import client.gui.core.Theme;
import client.gui.core.ThemeManager;
import client.gui.integration.Lab7CommandGateway;
import client.gui.integration.MockLab7CommandGateway;
import client.gui.pages.dashboard.prototype.DashboardContent;
import client.gui.pages.dashboard.prototype.DashboardNavigation;
import client.gui.pages.dashboard.prototype.DashboardTopBar;
import javafx.geometry.Orientation;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.SplitPane;

public class MainPrototypeView extends BorderPane {
    private final String currentUser;
    private final Lab7CommandGateway gateway;
    private final Runnable onLogout;

    private DashboardTopBar topBar;
    private DashboardNavigation navigation;
    private DashboardContent content;
    private Theme currentTheme = Theme.DARK;

    public MainPrototypeView(String currentUser) {
        this(currentUser, new MockLab7CommandGateway(), null);
    }

    public MainPrototypeView(String currentUser, Lab7CommandGateway gateway) {
        this(currentUser, gateway, null);
    }

    public MainPrototypeView(String currentUser, Lab7CommandGateway gateway, Runnable onLogout) {
        this.currentUser = currentUser == null || currentUser.isBlank() ? "demo_user" : currentUser;
        this.gateway = gateway == null ? new MockLab7CommandGateway() : gateway;
        this.onLogout = onLogout;

        getStyleClass().add("main-view");
        buildLayout();
    }

    private void buildLayout() {
        content = new DashboardContent(currentUser, gateway);
        navigation = new DashboardNavigation(currentUser, content::showSection, content::setStatus);
        topBar = new DashboardTopBar(
                currentUser,
                this::switchTheme,
                this::refreshLanguage,
                onLogout
        );

        SplitPane bodySplit = new SplitPane();
        bodySplit.getStyleClass().add("main-body-split");
        bodySplit.setOrientation(Orientation.HORIZONTAL);
        bodySplit.getItems().addAll(navigation, content);
        bodySplit.setDividerPositions(0.20);
        navigation.setMinWidth(180);
        navigation.setPrefWidth(230);

        setTop(topBar);
        setCenter(bodySplit);
    }

    private void switchTheme() {
        currentTheme = currentTheme == Theme.DARK ? Theme.LIGHT : Theme.DARK;
        if (getScene() != null) {
            ThemeManager.applyTheme(getScene().getRoot(), currentTheme);
        }
        topBar.setCurrentTheme(currentTheme);
    }

    private void refreshLanguage() {
        topBar.refreshLanguage();
        navigation.refreshLanguage();
        content.refreshLanguage();
    }
}
