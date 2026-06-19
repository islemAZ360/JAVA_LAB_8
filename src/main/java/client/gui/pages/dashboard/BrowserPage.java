package main.java.client.gui.pages.dashboard;

import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.layout.BasePage;
import main.java.client.gui.mockup.CartoonAnimationCanvas;

public class BrowserPage extends BasePage {
    public BrowserPage(Lab7CommandGateway gateway) {
        super("Film page", "film for fun");
        this.buildContent();
    }

    private void buildContent() {

        CartoonAnimationCanvas cartoonCanvas = new CartoonAnimationCanvas();
        cartoonCanvas.setWidth(1000);
        cartoonCanvas.setHeight(800);

//        Scene scene = new Scene(new StackPane(canvas), 800, 600);
//        primaryStage.setScene(scene);
//        primaryStage.show();
        this.wrapInScroll(cartoonCanvas);
    }
}
