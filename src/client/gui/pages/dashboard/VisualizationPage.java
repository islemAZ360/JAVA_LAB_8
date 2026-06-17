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

        this.gateway = gateway;
        buildContent();
    }

    private void buildContent() {
        // Canvas
        ObjectVisualizationCanvas canvas = new ObjectVisualizationCanvas();
        canvas.setWidth(760);
        canvas.setHeight(460);
        canvas.setItems(gateway.show());

        // Info label (shows selected object details)
        Label info = new Label(Messages.get(Messages.Key.VISUALIZATION_CLICK_HINT));
        info.setWrapText(true);
        info.getStyleClass().add("object-info");

        canvas.setOnObjectSelected(h -> info.setText(
                "id=" + h.id()
                        + ", name=" + h.name()
                        + ", owner=" + h.ownerLogin()
                        + ", coordinates=(" + h.coordinates().x() + "; " + h.coordinates().y() + ")"
                        + ", impactSpeed=" + h.impactSpeed()
        ));

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
        reload.setOnAction(e -> canvas.setItems(gateway.show()));

        // Canvas card
        UiCard canvasCard = new UiCard(
                Messages.get(Messages.Key.VISUALIZATION_CARD_TITLE),
                Messages.get(Messages.Key.VISUALIZATION_CARD_DESC)
        );
        canvasCard.content().getChildren().addAll(canvas, info, reload);

        this.getChildren().addAll(note, canvasCard);
    }
}
