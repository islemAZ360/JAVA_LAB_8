package client.gui.controller;

import client.gui.core.Messages;
import client.gui.core.Theme;
import client.gui.core.ThemeManager;
import client.gui.mockup.MainPrototypeView;
import client.gui.pages.auth.RegisterView;
import client.gui.router.AppRouter;
import client.gui.integration.Lab7CommandGateway;
import client.gui.integration.AuthResult;
import client.gui.pages.auth.LoginView;
import javafx.scene.Scene;

/**
 * AuthController - Handel logic authentication
 * Colab with AppRouter for navigation
 */
public class AuthController {

    private final Lab7CommandGateway gateway;
    private final AppRouter router;

    public AuthController(Lab7CommandGateway gateway, AppRouter router) {
        this.gateway = gateway;
        this.router = router;
    }

    public void handleLogin(LoginView loginView, String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            loginView.showMessage("Username and password cannot be empty");
            return;
        }

        AuthResult result = gateway.login(username, password);

        if (!result.success()) {
            loginView.showMessage(result.message());
            return;
        }

        router.showMainDashboard(result.username());
    }

    public void handleRegister(RegisterView registerView, String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            registerView.showMessage("Username and password cannot be empty");
            return;
        }

        AuthResult result = gateway.register(username, password);

        if (!result.success()) {
            registerView.showMessage(result.message());
            return;
        }

        registerView.showMessage(result.message());
        router.showMainDashboard(result.username());
    }
}
