package client.gui.pages.dashboard;

import client.gui.integration.Lab7CommandGateway;
import client.gui.layout.BasePage;
import client.gui.mockup.CartoonAnimationCanvas;

public class TerminalPage extends BasePage {
    public TerminalPage(Lab7CommandGateway gateway) {
        super("Film page", "film for fun");
        this.buildContent();
    }

    private void buildContent() {

        CartoonAnimationCanvas cartoonCanvas = new CartoonAnimationCanvas();
//        cartoonCanvas.setWidth(1000);
//        cartoonCanvas.setHeight(800);

//        Scene scene = new Scene(new StackPane(canvas), 800, 600);
//        primaryStage.setScene(scene);
//        primaryStage.show();

        this.getChildren().add(cartoonCanvas);
    }
}
