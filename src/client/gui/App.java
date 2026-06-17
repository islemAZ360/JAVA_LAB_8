package client.gui;

import client.gui.router.AppRouter;
import client.gui.core.CssLoader;
import client.gui.integration.Lab7CommandGateway;
import client.gui.integration.MockLab7CommandGateway;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main entry point of the JavaFX application.
 * Responsibilities:
 * - Create Stage and Scene
 * - Initialize AppRouter
 * - Start with Login screen
 */
public class App extends Application {

    private final Lab7CommandGateway gateway = new MockLab7CommandGateway();

    @Override
    public void start(Stage stage) {
        stage.setTitle("HumanBeing GUI");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);

        // Create root Scene with empty StackPane
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1100, 720);
        CssLoader.applyTo(scene);
        stage.setScene(scene);

        // Initialize router and navigate to Login screen
        AppRouter router = new AppRouter(stage, gateway);
        router.showLogin();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
