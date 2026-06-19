package main.java.client.gui.controllers;

import main.java.client.gui.pages.auth.RegisterView;
import main.java.client.gui.router.AppRouter;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.integration.AuthResult;
import main.java.client.gui.pages.auth.LoginView;

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
