package client.gui.pages.dashboard;

import client.gui.integration.Lab7CommandGateway;
import client.gui.layout.BasePage;
import client.gui.mockup.CartoonAnimationCanvas;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FilmPage extends BasePage {
    public FilmPage(Lab7CommandGateway gateway) {
        super("Film page", "film for fun");
        this.buildContent();
    }

    private void buildContent() {
        CartoonAnimationCanvas cartoonCanvas = new CartoonAnimationCanvas();
        StackPane filmView = new StackPane(cartoonCanvas);
        filmView.setMinSize(0, 0);
        filmView.setPrefSize(800, 600);

//        cartoonCanvas.setWidth(800);
//        cartoonCanvas.setHeight(600);
//        filmView.setPrefSize(800, 600);

//        Scene scene = new Scene(new StackPane(canvas), 800, 600);
//        primaryStage.setScene(scene);
//        primaryStage.show();

//        cartoonCanvas.widthProperty().bind(filmView.widthProperty());
//        cartoonCanvas.heightProperty().bind(filmView.heightProperty());

        VBox.setVgrow(filmView, Priority.ALWAYS);
        VBox.setVgrow(filmView, Priority.ALWAYS);

//        setVGrow(filmView);
        this.getChildren().add(filmView);
    }
}
