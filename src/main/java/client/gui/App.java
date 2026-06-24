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
/// **
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

import javafx.scene.image.Image;
import main.java.client.gui.core.ConfigLoader;
import main.java.client.gui.core.CssLoader;
import main.java.client.gui.integration.*;
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
        ConfigLoader configLoader = new ConfigLoader("/main/resources/config/config.properties");
        stage.setTitle(configLoader.getString("window.title"));
        Image appIcon = new Image(configLoader.getString("window.icon"));
        stage.getIcons().add(appIcon);
        stage.setMinWidth(configLoader.getInt("window.minWidth"));
        stage.setMinHeight(configLoader.getInt("window.minHeight"));
        StackPane root = new StackPane();
        Scene scene = new Scene(root, configLoader.getInt("window.width"), configLoader.getInt("window.height"));
        CssLoader.applyTo(scene);
        stage.setScene(scene);
        Lab7CommandGateway gateway = resolveGateway();
        LLMGateWay llmGateway = resolveLlmGateway();
        LLMGatewayStream streamGateway = resolveLlmGatewayStream();
        AppRouter router = new AppRouter(stage, gateway, llmGateway, streamGateway);
        router.showLogin();
        stage.setOnCloseRequest(event -> {
            javafx.application.Platform.exit(); // Close all thread JavaFX
            Platform.exit();
            System.exit(0);
        });
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
                alert.setContentText("Приложение запущено в демо-режиме.\n" + "Данные не сохраняются.\n\n" + "Причина: " + e.getMessage());
                alert.showAndWait();
            });

            return new MockLab7CommandGateway();
        }
    }

    private LLMGateWay resolveLlmGateway() {
        try {
            LLMGateWay real = new LLMGateWay();
            System.out.println("[App] Connected to main.java.server → InputManagerGateway");
            return real;
        } catch (Exception e) {
            System.err.println("[App] Server unavailable (" + e.getMessage() + ") → MockLab7CommandGateway");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Сервер недоступен");
                alert.setHeaderText("Нет соединения с сервером");
                alert.setContentText("Приложение запущено в демо-режиме.\n" + "Данные не сохраняются.\n\n" + "Причина: " + e.getMessage());
                alert.showAndWait();
            });

            return new LLMGateWay();
        }
    }

    private LLMGatewayStream resolveLlmGatewayStream() {
        try {
            LLMGatewayStream real = new LLMGatewayStream();
            System.out.println("[App] Connected to main.java.server → InputManagerGateway");
            return real;
        } catch (Exception e) {
            System.err.println("[App] Server unavailable (" + e.getMessage() + ") → MockLab7CommandGateway");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Сервер недоступен");
                alert.setHeaderText("Нет соединения с сервером");
                alert.setContentText("Приложение запущено в демо-режиме.\n" + "Данные не сохраняются.\n\n" + "Причина: " + e.getMessage());
                alert.showAndWait();
            });

            return new LLMGatewayStream();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
