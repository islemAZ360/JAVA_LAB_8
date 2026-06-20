package main.java.client.gui.pages.dashboard;

import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.card.UiCard;
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
        CartoonAnimationCanvas canvas = new CartoonAnimationCanvas();
        canvas.getStyleClass().add("cartoon-canvas");

        // канвас внутри StackPane, чтобы центрировался и растягивался
        StackPane filmView = new StackPane(canvas);
        filmView.setMinSize(0, 0);
        filmView.setPrefSize(800, 500);
        VBox.setVgrow(filmView, Priority.ALWAYS);

        // панель управления анимацией
        UiButton playPauseBtn = new UiButton("Pause", ButtonVariant.OUTLINE);
        UiButton speedX1Btn = new UiButton("Speed x1", ButtonVariant.GHOST);
        UiButton speedX2Btn = new UiButton("Speed x2", ButtonVariant.GHOST);

        playPauseBtn.setOnAction(e -> {
            canvas.togglePlay();
            playPauseBtn.setText(canvas.isPlaying() ? "Pause" : "Play");
        });

        speedX1Btn.setOnAction(e -> {
            canvas.setSpeed(1.0);
            speedX1Btn.applyVariant(ButtonVariant.OUTLINE);
            speedX2Btn.applyVariant(ButtonVariant.GHOST);
        });

        speedX2Btn.setOnAction(e -> {
            canvas.setSpeed(2.0);
            speedX2Btn.applyVariant(ButtonVariant.OUTLINE);
            speedX1Btn.applyVariant(ButtonVariant.GHOST);
        });

        HBox controlBar = new HBox(10, playPauseBtn, speedX1Btn, speedX2Btn);
        controlBar.getStyleClass().add("canvas-control");
        controlBar.setAlignment(Pos.CENTER);

        // упаковываем канвас и панель управления в карточку
        UiCard filmCard = new UiCard(
                Messages.get(Messages.Key.PAGE_FILM_TITLE),
                "Interactive cartoon animation with playback controls"
        );
        filmCard.getStyleClass().add("film-card");
        filmCard.content().getChildren().addAll(filmView, controlBar);

        this.getChildren().add(filmCard);
        VBox.setVgrow(filmCard, Priority.ALWAYS);
    }
}
