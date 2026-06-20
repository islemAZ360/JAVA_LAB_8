package main.java.client.gui.pages.dashboard;

import main.java.client.gui.components.alert.AlertVariant;
import main.java.client.gui.components.alert.UiAlert;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.card.UiCard;
import main.java.client.gui.core.Messages;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.mockup.ObjectVisualizationCanvas;
import main.java.client.gui.layout.BasePage;
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
        // единственный радар-канвас на всю ширину страницы
        ObjectVisualizationCanvas mainCanvas = new ObjectVisualizationCanvas();
        mainCanvas.getStyleClass().add("main-canvas");
        mainCanvas.setWidth(900);
        mainCanvas.setHeight(500);
        mainCanvas.setItems(gateway.show());

        // область под канвасом: информация о обнаруженном объекте + кнопка сброса
        Label objectDetectedInfo = new Label(Messages.get(Messages.Key.OBJECTS_DETECTED_BY_RADAR));
        objectDetectedInfo.getStyleClass().add("object-info");

        UiButton resetOriginBtn = new UiButton(
                Messages.get(Messages.Key.VISUALIZATION_RESET_ORIGIN),
                ButtonVariant.DEFAULT
        );
        resetOriginBtn.setOnAction(e -> mainCanvas.resetOrigin());

        VBox objectDetectedArea = new VBox(10, objectDetectedInfo, resetOriginBtn);
        objectDetectedArea.getStyleClass().add("object-detected-area");

        // лейбл с деталями выбранного объекта
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

        mainCanvas.setOnObjectDetected(h -> objectDetectedInfo.setText(
                "Detected: " + h.name()
                + ", pos=(" + h.coordinates().x() + "; " + h.coordinates().y() + ")"
                + ", speed=" + h.impactSpeed() + " m/s"
        ));

        // инфо-алерт
        UiAlert note = new UiAlert(
                Messages.get(Messages.Key.VISUALIZATION_ANIMATION_TITLE),
                Messages.get(Messages.Key.VISUALIZATION_ANIMATION_DESC),
                AlertVariant.INFO
        );

        // кнопка перезагрузки данных
        UiButton reload = new UiButton(
                Messages.get(Messages.Key.VISUALIZATION_RELOAD),
                ButtonVariant.DEFAULT
        );
        reload.setOnAction(e -> mainCanvas.setItems(gateway.show()));

        // карточка с канвасом
        UiCard canvasCard = new UiCard(
                Messages.get(Messages.Key.VISUALIZATION_CARD_TITLE),
                Messages.get(Messages.Key.VISUALIZATION_CARD_DESC)
        );
        canvasCard.getStyleClass().add("canvas-card");

        // панель управления: инфо о выборе + кнопка reload + кнопка сброса
        HBox canvasControl = new HBox(12, info, reload);
        canvasControl.getStyleClass().add("canvas-control");

        // канвас занимает всю ширину, под ним — панель управления и область обнаружения
        VBox canvasLayout = new VBox(14, mainCanvas, canvasControl, objectDetectedArea);
        VBox.setVgrow(mainCanvas, Priority.ALWAYS);

        canvasCard.content().getChildren().add(canvasLayout);

        this.getChildren().addAll(note, canvasCard);
    }
}
