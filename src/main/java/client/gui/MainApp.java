package main.java.client.gui;

import main.java.client.gui.core.Theme;
import main.java.client.gui.core.ThemeManager;
import main.java.client.gui.integration.AuthResult;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.integration.MockLab7CommandGateway;
import main.java.client.gui.mockup.MainPrototypeView;
import main.java.client.gui.pages.auth.RegisterView;
import javafx.application.Application;
import javafx.scene.Parent;
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

//    private void openLoginWindow(Stage stage) {
//        LoginView loginView = new LoginView();
//        Scene scene = stage.getScene();
//
//        if (scene == null) {
//            scene = new Scene(loginView, 520, 420);
//            CssLoader.applyTo(scene);
//            stage.setScene(scene);
//        } else {
//            scene.setRoot(loginView);
//        }
//        ThemeManager.applyTheme(scene.getRoot(), Theme.DARK);
//
//        loginView.setOnLogin((login, password) -> {
//            AuthResult result = gateway.login(login, password);
//            if (!result.success()) {
//                loginView.showMessage(result.message());
//                return;
//            }
//            currentUser = normalizeUsername(result.username());
//            openDashboard(stage);
//        });
//
//        loginView.setOnRegister((login, password) -> {
//            AuthResult result = gateway.register(login, password);
//            loginView.showMessage(result.message());
//            if (result.success()) {
//                currentUser = normalizeUsername(result.username());
//                openDashboard(stage);
//            }
//        });
//    }

    private void openLoginWindow(Stage stage) {

    }

    // ✅ MỚI: Mở RegisterView
    private void openRegisterWindow(Stage stage) {
        RegisterView registerView = new RegisterView();
        registerView.setMaxWidth(520);
        registerView.setMaxHeight(560);

        Scene scene = stage.getScene();
        scene.setRoot(registerView);
        applyUiSettings(scene.getRoot());

        // ✅ Xử lý Register
        registerView.setOnRegister((login, password) -> {
            AuthResult result = gateway.register(login, password);
            if (!result.success()) {
                registerView.showServerError(result.message());
                return;
            }
            currentUser = normalizeUsername(result.username());
            openMainDashboard(stage);
        });

        // ✅ Chuyển về LoginView
        registerView.setOnSwitchToLogin(() -> openLoginWindow(stage));
    }

    private void openMainDashboard(Stage stage) {
    }

    private void applyUiSettings(Parent root) {
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


