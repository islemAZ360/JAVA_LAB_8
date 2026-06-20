package main.java.client.gui.pages.dashboard;

import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.core.Messages;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.layout.BasePage;
import main.java.client.gui.mockup.CartoonAnimationCanvas;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FilmPage extends BasePage {

    public FilmPage(Lab7CommandGateway gateway) {
        super(
                Messages.get(Messages.Key.PAGE_FILM_TITLE),
                Messages.get(Messages.Key.PAGE_FILM_TITLE));
        this.buildContent();
    }

    private void buildContent() {
        this.getStyleClass().add("film-page");

        CartoonAnimationCanvas canvas = new CartoonAnimationCanvas();
        canvas.getStyleClass().add("cartoon-canvas");

        // контейнер канваса: тёмный фон, скруглённые углы, тень
        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.getStyleClass().add("film-canvas-container");
        canvasContainer.setMinSize(0, 0);
        VBox.setVgrow(canvasContainer, Priority.ALWAYS);

        // панель управления строго под канвасом, не перекрывает его
        UiButton playPauseBtn = new UiButton("Pause", ButtonVariant.SECONDARY);
        UiButton speedX1Btn = new UiButton("Speed x1", ButtonVariant.SECONDARY);
        UiButton speedX2Btn = new UiButton("Speed x2", ButtonVariant.GHOST);

        playPauseBtn.setOnAction(e -> {
            canvas.togglePlay();
            playPauseBtn.setText(canvas.isPlaying() ? "Pause" : "Play");
        });

        speedX1Btn.setOnAction(e -> {
            canvas.setSpeed(1.0);
            speedX1Btn.applyVariant(ButtonVariant.SECONDARY);
            speedX2Btn.applyVariant(ButtonVariant.GHOST);
        });

        speedX2Btn.setOnAction(e -> {
            canvas.setSpeed(2.0);
            speedX2Btn.applyVariant(ButtonVariant.SECONDARY);
            speedX1Btn.applyVariant(ButtonVariant.GHOST);
        });

        HBox controlBar = new HBox(12, playPauseBtn, speedX1Btn, speedX2Btn);
        controlBar.getStyleClass().add("film-control-bar");
        controlBar.setAlignment(Pos.CENTER);

        // общий макет: канвас сверху, панель управления снизу
        VBox playerLayout = new VBox(14, canvasContainer, controlBar);
        playerLayout.getStyleClass().add("film-player-layout");

        this.getChildren().add(playerLayout);
        VBox.setVgrow(playerLayout, Priority.ALWAYS);
    }
}
