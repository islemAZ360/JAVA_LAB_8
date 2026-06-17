package client.gui.router;

import client.gui.layout.MainShell;
import client.gui.controller.AuthController;
import client.gui.pages.auth.LoginView;
import client.gui.pages.auth.RegisterView;
import client.gui.pages.dashboard.PrototypePage;
import client.gui.pages.dashboard.ComponentShowcasePage;
import client.gui.pages.dashboard.DataTablePage;
import client.gui.pages.dashboard.SettingsPage;
import client.gui.pages.dashboard.VisualizationPage;
import client.gui.core.Direction;
import client.gui.core.Messages;
import client.gui.core.RtlSupport;
import client.gui.core.Theme;
import client.gui.core.ThemeManager;
import client.gui.integration.Lab7CommandGateway;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * AppRouter - Handles navigation between screens.
 * Responsibilities:
 * - Switch between Auth screens (Login/Register)
 * - Switch between Dashboard pages
 * - Apply theme/direction settings globally
 */
public class AppRouter {

    private final Stage stage;
    private final Lab7CommandGateway gateway;
    private MainShell currentShell;

    private Theme currentTheme = Theme.DARK;
    private Direction currentDirection = Direction.LTR;
    private AuthController authController;

    public AppRouter(Stage stage, Lab7CommandGateway gateway) {
        this.stage = stage;
        this.gateway = gateway;
        this.authController = new AuthController(gateway, this);
    }

    // ========================================
    // AUTH ROUTING
    // ========================================

    public void showLogin() {
        LoginView loginView = new LoginView();

        loginView.setOnLogin((username, password) -> {
            this.authController.handleLogin(loginView, username, password);
        });

        Scene scene = stage.getScene();
        scene.setRoot(loginView);
        applyUiSettings(scene.getRoot());

        // Navigation callbacks
        loginView.setOnSwitchToRegister(this::showRegister);
    }

    public void showRegister() {
        RegisterView registerView = new RegisterView();

        registerView.setOnRegister((username, password) -> {
            this.authController.handleRegister(registerView, username, password);
        });

        Scene scene = stage.getScene();
        scene.setRoot(registerView);
        applyUiSettings(scene.getRoot());

        // Navigation callbacks
        registerView.setOnSwitchToLogin(this::showLogin);
    }

    // ========================================
    // DASHBOARD ROUTING
    // ========================================

    public void showMainDashboard(String username) {
        // Create MainShell
        currentShell = new MainShell(stage, username);

        // Register menu items with navigation
        currentShell.addItem(Messages.get(Messages.Key.MENU_PROTOTYPE),
                () -> showPrototype());
        currentShell.addItem(Messages.get(Messages.Key.MENU_COMPONENTS),
                () -> showComponentShowcase());
        currentShell.addItem(Messages.get(Messages.Key.MENU_DATA_TABLE),
                () -> showDataTable());
        currentShell.addItem(Messages.get(Messages.Key.MENU_VISUALIZATION),
                () -> showVisualization());
        currentShell.addItem(Messages.get(Messages.Key.MENU_SETTINGS),
                () -> showSettings());

        // Logout callback
        currentShell.setOnLogout(this::showLogin);

        // Display shell
        Scene scene = stage.getScene();
        scene.setRoot(currentShell);
        applyUiSettings(currentShell);

        // Default page
        showPrototype();
    }

    // ========================================
    // PAGE ROUTING
    // ========================================

    private void showPrototype() {
        currentShell.setContent(
                new PrototypePage(currentShell.getCurrentUser(), gateway, this::showLogin)
        );
    }

    private void showComponentShowcase() {
        currentShell.setContent(new ComponentShowcasePage());
    }

    private void showDataTable() {
        currentShell.setContent(new DataTablePage(gateway));
    }

    private void showVisualization() {
        currentShell.setContent(new VisualizationPage(gateway));
    }

    private void showSettings() {
        SettingsPage settingsPage = new SettingsPage();

        // Theme change callback
        settingsPage.setOnThemeChange(theme -> {
            this.currentTheme = theme;
            applyUiSettings(currentShell);
        });

        // Direction change callback
        settingsPage.setOnDirectionChange(direction -> {
            this.currentDirection = direction;
            applyUiSettings(currentShell);
        });

        currentShell.setContent(settingsPage);
    }

    // ========================================
    // HELPERS
    // ========================================

    private void applyUiSettings(javafx.scene.Parent root) {
        ThemeManager.applyTheme(root, currentTheme);
        RtlSupport.applyToRoot(root, currentDirection);
    }
}
