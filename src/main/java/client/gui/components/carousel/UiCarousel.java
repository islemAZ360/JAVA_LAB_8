package main.java.client.gui.components.carousel;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.util.Duration;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import java.util.List;

public class UiCarousel extends BorderPane {
    private final List<Node> slides;
    private int current = 0;
    private boolean loop = false;
    private final StackPane viewport = new StackPane();

    public UiCarousel(List<Node> slides) {
        getStyleClass().add("ui-carousel");
        this.slides = slides;

        viewport.getStyleClass().add("ui-carousel-viewport");
        viewport.setClip(new javafx.scene.shape.Rectangle(0, 0, Double.MAX_VALUE, Double.MAX_VALUE));
        viewport.widthProperty().addListener((obs, o, w) ->
                ((javafx.scene.shape.Rectangle) viewport.getClip()).setWidth(w.doubleValue()));
        viewport.heightProperty().addListener((obs, o, h) ->
                ((javafx.scene.shape.Rectangle) viewport.getClip()).setHeight(h.doubleValue()));

        // Đã sửa đổi ở đây bằng cách truyền 2 tham số
        UiButton prev = new UiButton("‹", ButtonVariant.OUTLINE);
        UiButton next = new UiButton("›", ButtonVariant.OUTLINE);

        prev.getStyleClass().add("ui-carousel-prev");
        next.getStyleClass().add("ui-carousel-next");
        prev.setOnAction(e -> go(current - 1));
        next.setOnAction(e -> go(current + 1));

        setLeft(prev);
        setCenter(viewport);
        setRight(next);
        BorderPane.setAlignment(prev, Pos.CENTER);
        BorderPane.setAlignment(next, Pos.CENTER);

        if (!slides.isEmpty()) showSlide(0, 0);
    }

    public UiCarousel setLoop(boolean loop) { this.loop = loop; return this; }

    public void go(int index) {
        if (slides.isEmpty()) return;
        int next = loop ? Math.floorMod(index, slides.size())
                : Math.max(0, Math.min(index, slides.size() - 1));
        if (next == current) return;
        int dir = next > current ? 1 : -1;
        showSlide(next, dir);
        current = next;
    }

    private void showSlide(int index, int dir) {
        Node slide = slides.get(index);
        if (dir != 0 && !viewport.getChildren().isEmpty()) {
            Node old = viewport.getChildren().get(0);
            double w = viewport.getWidth();
            slide.setTranslateX(w * dir);
            viewport.getChildren().add(slide);
            TranslateTransition animIn  = new TranslateTransition(Duration.millis(300), slide);
            TranslateTransition animOut = new TranslateTransition(Duration.millis(300), old);
            animIn.setToX(0);
            animOut.setToX(-w * dir);
            animOut.setOnFinished(e -> viewport.getChildren().remove(old));
            animIn.play(); animOut.play();
        } else {
            viewport.getChildren().setAll(slide);
        }
    }

    public int getCurrentIndex() { return current; }
}
