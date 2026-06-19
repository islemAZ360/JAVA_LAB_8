//package main.java.client.gui;
//
//import main.java.client.gui.router.AppRouter;
//import main.java.client.gui.core.CssLoader;
//import main.java.client.gui.integration.Lab7CommandGateway;
//import main.java.client.gui.integration.MockLab7CommandGateway;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//
///**
// * Main entry point of the JavaFX application.
// * Responsibilities:
// * - Create Stage and Scene
// * - Initialize AppRouter
// * - Start with Login screen
// */
//public class App extends Application {
//
//    private final Lab7CommandGateway gateway = new MockLab7CommandGateway();
//
//    @Override
//    public void start(Stage stage) {
//        stage.setTitle("HumanBeing GUI");
//        stage.setMinWidth(1100);
//        stage.setMinHeight(720);
//
//        // Create root Scene with empty StackPane
//        StackPane root = new StackPane();
//        Scene scene = new Scene(root, 1100, 720);
//        CssLoader.applyTo(scene);
//        stage.setScene(scene);
//
//        // Initialize router and navigate to Login screen
//        AppRouter router = new AppRouter(stage, gateway);
//        router.showLogin();
//
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}


package main.java.client.gui;

import main.java.client.gui.core.CssLoader;
import main.java.client.gui.integration.InputManagerGateway;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.integration.MockLab7CommandGateway;
import main.java.client.gui.router.AppRouter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main entry point of the JavaFX application.
 * 1. Create Stage + Scene
 * 2. Select gateway (real or mock)
 * 3. Initialize AppRouter and start at Login
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("HumanBeing GUI");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1100, 720);
        CssLoader.applyTo(scene);
        stage.setScene(scene);
        Lab7CommandGateway gateway = resolveGateway();
        AppRouter router = new AppRouter(stage, gateway);
        router.showLogin();
        stage.show();
    }
    /**
     * Tries to connect to the real main.java.server.
     * If it fails -> alerts the user and falls back to Mock.
     */
    private Lab7CommandGateway resolveGateway() {
        try {
            Lab7CommandGateway real = InputManagerGateway.connectDefault();
            System.out.println("[App] Connected to main.java.server → InputManagerGateway");
            return real;
        } catch (Exception e) {
            System.err.println("[App] Server unavailable (" + e.getMessage() + ") → MockLab7CommandGateway");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Сервер недоступен");
                alert.setHeaderText("Нет соединения с сервером");
                alert.setContentText(
                        "Приложение запущено в демо-режиме.\n" +
                                "Данные не сохраняются.\n\n" +
                                "Причина: " + e.getMessage()
                );
                alert.showAndWait();
            });

            return new MockLab7CommandGateway();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
