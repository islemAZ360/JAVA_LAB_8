package client.ui.template.mockup;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import client.ui.template.model.HumanBeingUiModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ObjectVisualizationCanvas extends Canvas {
    private final List<HumanBeingUiModel> items = new ArrayList<>();
    private final DoubleProperty animationScale = new SimpleDoubleProperty(1.0);
    private Consumer<HumanBeingUiModel> onObjectSelected = h -> {};

    public ObjectVisualizationCanvas() {
        getStyleClass().add("object-canvas");
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
        animationScale.addListener(e -> draw());
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);
    }

    public void setItems(Collection<HumanBeingUiModel> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        animateAppearance();
    }

    public void setOnObjectSelected(Consumer<HumanBeingUiModel> onObjectSelected) {
        this.onObjectSelected = onObjectSelected == null ? h -> {} : onObjectSelected;
    }

    private void animateAppearance() {
        animationScale.set(0.1);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(animationScale, 0.1)),
                new KeyFrame(Duration.millis(450), new KeyValue(animationScale, 1.0))
        );
        timeline.play();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();
        gc.clearRect(0, 0, w, h);

        gc.setFill(Color.web("#111827"));
        gc.fillRoundRect(0, 0, w, h, 18, 18);

        drawGrid(gc, w, h);

        for (HumanBeingUiModel item : items) {
            drawObject(gc, item);
        }
    }

    private void drawGrid(GraphicsContext gc, double w, double h) {
        gc.setStroke(Color.web("#374151"));
        gc.setLineWidth(1);
        for (int x = 0; x < w; x += 40) gc.strokeLine(x, 0, x, h);
        for (int y = 0; y < h; y += 40) gc.strokeLine(0, y, w, y);

        gc.setStroke(Color.web("#6b7280"));
        gc.strokeLine(w / 2, 0, w / 2, h);
        gc.strokeLine(0, h / 2, w, h / 2);
    }

    private void drawObject(GraphicsContext gc, HumanBeingUiModel item) {
        double[] xy = toCanvas(item);
        double radius = radiusOf(item) * animationScale.get();
        Color color = ownerColor(item.ownerLogin());

        gc.setGlobalAlpha(0.85);
        gc.setFill(color);
        gc.fillOval(xy[0] - radius, xy[1] - radius, radius * 2, radius * 2);
        gc.setGlobalAlpha(1.0);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(xy[0] - radius, xy[1] - radius, radius * 2, radius * 2);

        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(item.id()), xy[0] - 4, xy[1] + 4);
    }

    private void handleClick(MouseEvent event) {
        for (HumanBeingUiModel item : items) {
            double[] xy = toCanvas(item);
            double radius = radiusOf(item);
            double dx = event.getX() - xy[0];
            double dy = event.getY() - xy[1];
            if (dx * dx + dy * dy <= radius * radius) {
                onObjectSelected.accept(item);
                return;
            }
        }
    }

    private double[] toCanvas(HumanBeingUiModel item) {
        double scale = 28;
        double x = getWidth() / 2 + item.coordinates().x() * scale;
        double y = getHeight() / 2 - item.coordinates().y() * scale;
        return new double[]{x, y};
    }

    private double radiusOf(HumanBeingUiModel item) {
        return Math.max(12, Math.min(42, 10 + item.impactSpeed()));
    }

    private Color ownerColor(String owner) {
        int hash = owner == null ? 0 : Math.abs(owner.hashCode());
        return Color.hsb(hash % 360, 0.72, 0.92);
    }
}
