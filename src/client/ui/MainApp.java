package client.ui;

import client.ui.template.core.CssLoader;
import client.ui.template.core.Theme;
import client.ui.template.core.ThemeManager;
import client.ui.template.integration.AuthResult;
import client.ui.template.integration.Lab7CommandGateway;
import client.ui.template.integration.MockLab7CommandGateway;
import client.ui.template.mockup.LoginView;
import client.ui.template.mockup.MainPrototypeView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private final Lab7CommandGateway gateway = new MockLab7CommandGateway();
    private String currentUser = "demo_user";

    @Override
    public void start(Stage stage) {
        openLoginWindow(stage);
        stage.setTitle("HumanBeing GUI");
        stage.setMinWidth(1080);
        stage.setMinHeight(720);
        stage.show();
    }

    private void openLoginWindow(Stage stage) {
        LoginView loginView = new LoginView();
        Scene scene = stage.getScene();

        if (scene == null) {
            scene = new Scene(loginView, 520, 420);
            CssLoader.applyTo(scene);
            stage.setScene(scene);
        } else {
            scene.setRoot(loginView);
        }
        ThemeManager.applyTheme(scene.getRoot(), Theme.DARK);

        loginView.setOnLogin((login, password) -> {
            AuthResult result = gateway.login(login, password);
            if (!result.success()) {
                loginView.showMessage(result.message());
                return;
            }
            currentUser = normalizeUsername(result.username());
            openDashboard(stage);
        });

        loginView.setOnRegister((login, password) -> {
            AuthResult result = gateway.register(login, password);
            loginView.showMessage(result.message());
            if (result.success()) {
                currentUser = normalizeUsername(result.username());
                openDashboard(stage);
            }
        });
    }

    private void openDashboard(Stage stage) {
        MainPrototypeView view = new MainPrototypeView(currentUser, gateway, () -> openLoginWindow(stage));
        Scene scene = stage.getScene();
        scene.setRoot(view);
        ThemeManager.applyTheme(scene.getRoot(), Theme.DARK);
    }

    private String normalizeUsername(String username) {
        return username == null || username.isBlank() ? "demo_user" : username.trim();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
