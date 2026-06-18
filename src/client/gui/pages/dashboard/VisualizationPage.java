package client.gui.pages.dashboard;

import client.gui.components.alert.AlertVariant;
import client.gui.components.alert.UiAlert;
import client.gui.components.button.ButtonVariant;
import client.gui.components.button.UiButton;
import client.gui.components.card.UiCard;
import client.gui.core.Messages;
import client.gui.integration.Lab7CommandGateway;
import client.gui.mockup.ObjectVisualizationCanvas;
import client.gui.layout.BasePage;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Page 4: Canvas Visualization.
 * Renders HumanBeing objects as graphical primitives.
 */
public class VisualizationPage extends BasePage {

    private final Lab7CommandGateway gateway;

    public VisualizationPage(Lab7CommandGateway gateway) {
        super(
                Messages.get(Messages.Key.PAGE_VISUALIZATION_TITLE),
                Messages.get(Messages.Key.PAGE_VISUALIZATION_DESCRIPTION)
        );

        this.getStyleClass().add("visualization-page");
        this.gateway = gateway;
        buildContent();
    }

    private void buildContent() {
//        Canvas
        ObjectVisualizationCanvas mainCanvas = new ObjectVisualizationCanvas();
        mainCanvas.getStyleClass().add("main-canvas");
//        Wrote in css file
        mainCanvas.setWidth(900);
        mainCanvas.setHeight(500);
//        Canvas can not autoscale
//        mainCanvas.prefWidth(Double.MAX_VALUE);
//        mainCanvas.prefHeight(Double.MAX_VALUE);
        mainCanvas.setItems(gateway.show());

        VBox objectDetectedArea = new VBox(20);
        objectDetectedArea.getStyleClass().add("object-detected-area");
        Label objectDetectedInfo = new Label(Messages.get(Messages.Key.OBJECTS_DETECTED_BY_RADAR));

        // Reset origin button
        UiButton resetOriginBtn = new UiButton(
                Messages.get(Messages.Key.VISUALIZATION_RESET_ORIGIN),
                ButtonVariant.DEFAULT
        );
        resetOriginBtn.setOnAction(e -> mainCanvas.resetOrigin());
        objectDetectedArea.getChildren().addAll(objectDetectedInfo, resetOriginBtn);

        ObjectVisualizationCanvas cartoonCanvas = new ObjectVisualizationCanvas();
        cartoonCanvas.getStyleClass().add("cartoon-canvas");
        cartoonCanvas.setWidth(400);
        cartoonCanvas.setHeight(350);

        VBox rightVisualArea = new VBox(20);
        rightVisualArea.getStyleClass().add("right-visual-area");
        rightVisualArea.getChildren().addAll(cartoonCanvas, objectDetectedArea);

        HBox canvasWrapper = new HBox(10);
        canvasWrapper.getChildren().addAll(mainCanvas,rightVisualArea);
        canvasWrapper.getStyleClass().add("canvas-wrapper");
        HBox.setHgrow(mainCanvas, Priority.ALWAYS);

        // Info label (shows selected object details)
        Label info = new Label(Messages.get(Messages.Key.VISUALIZATION_CLICK_HINT));
        info.setWrapText(true);
        info.getStyleClass().add("object-info");

        mainCanvas.setOnObjectSelected(h -> info.setText(
                "id=" + h.id()
                + ", name=" + h.name()
                + ", owner=" + h.ownerLogin()
                + ", coordinates=(" + h.coordinates().x() + "; " + h.coordinates().y() + ")"
                + ", impactSpeed=" + h.impactSpeed()
        ));

//        show to text area
        mainCanvas.setOnObjectDetected(h -> {
            objectDetectedInfo.setText(
                    "Detected: " + h.name()
                    + ", pos=(" + h.coordinates().x()
                    + "; " + h.coordinates().y()
                    + ")" + ", speed=" + h.impactSpeed() + " m/s"
            );
        });

        // Info alert
        UiAlert note = new UiAlert(
                Messages.get(Messages.Key.VISUALIZATION_ANIMATION_TITLE),
                Messages.get(Messages.Key.VISUALIZATION_ANIMATION_DESC),
                AlertVariant.INFO
        );

        // Reload button
        UiButton reload = new UiButton(
                Messages.get(Messages.Key.VISUALIZATION_RELOAD),
                ButtonVariant.DEFAULT
        );
        reload.setOnAction(e -> mainCanvas.setItems(gateway.show()));

        // Canvas card
        UiCard canvasCard = new UiCard(
                Messages.get(Messages.Key.VISUALIZATION_CARD_TITLE),
                Messages.get(Messages.Key.VISUALIZATION_CARD_DESC)
        );
        canvasCard.getStyleClass().add("canvas-card");

        HBox canvasControl = new HBox(12, info, reload);
        canvasControl.getStyleClass().add("canvas-control");

        canvasCard.content().getChildren().addAll(wrapInScroll(canvasWrapper), canvasControl);

        this.getChildren().addAll(note, canvasCard);
    }
}
