package main.java.client.gui.mockup;

import main.java.client.gui.core.OwnerColorPalette;
import main.java.client.gui.model.HumanBeingUiModel;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.function.Consumer;

public class ObjectVisualizationCanvas extends Canvas {
    private final List<HumanBeingUiModel> items = new ArrayList<>();
    private final DoubleProperty animationScale = new SimpleDoubleProperty(1.0);

    private Consumer<HumanBeingUiModel> onObjectSelected = h -> {
    };
    private Consumer<HumanBeingUiModel> onObjectDetected = h -> {
    };

    // Memory to mark whether the radar has already swept over the object in the current sweep
    // (prevents triggering the callback dozens of times continuously as the tail passes)
    private final Map<Long, Boolean> detectedInCurrentSweep = new HashMap<>();
    private OptionalLong selectedId = OptionalLong.empty();

    private double offsetX = 0;
    private double offsetY = 0;
    private double mouseAnchorX;
    private double mouseAnchorY;
    private boolean isDragging = false;

    private double radarAngle = 0;
    private final int TAIL_SIZE = 60;

    private ImagePattern gridPattern = null;
    private WritableImage radarSweepImage = null; // ADD THIS VARIABLE: Radar sweep image buffer
    private final double RADAR_MAX_SIZE = 1200;

    // ADD THIS VARIABLE: Callback to handle when the radar sweeps over an object

    // Memory to store the current radius of each Object to smooth the zoom in/out animation effect
    private final Map<Long, Double> animatedRadiusMap = new HashMap<>();

    public ObjectVisualizationCanvas() {
        getStyleClass().add("object-canvas");

        // Create grid 1 time and reuse to produce rerendering for optimizing cpu (6.5%)
        createGridPattern();
        createRadarSweepImage();

        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
        animationScale.addListener(e -> draw());

        addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);

        startRadarAnimation();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    private void startRadarAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //               rotation speed
                radarAngle = (radarAngle + 0.4) % 360;
                draw();
            }
        };
        timer.start();
    }

    public void setItems(Collection<HumanBeingUiModel> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        animateAppearance();
    }

    public void setSelectedObject(HumanBeingUiModel selected) {
        selectedId = selected == null ? OptionalLong.empty() : OptionalLong.of(selected.id());
        draw();
    }

    public void setOnObjectSelected(Consumer<HumanBeingUiModel> onObjectSelected) {
        this.onObjectSelected = onObjectSelected == null ? h -> {
        } : onObjectSelected;
    }

    /**
     * Đưa hệ tọa độ và tâm Radar quay trở về chính giữa Canvas (Reset Pan/Drag)
     */
    public void resetOrigin() {
        // 1. Reset translation offset to 0 to bring the center back to the middle of the screen
        this.offsetX = 0;
        this.offsetY = 0;

        // 2. Clear the old animated radius memory
        // This is required so that the HumanBeings shrink back to minRadius and re-apply the smooth effect at the new center position
        this.animatedRadiusMap.clear();
        this.detectedInCurrentSweep.clear();

        // 3. Trigger an immediate UI redraw if necessary
        draw();
    }

    // ADD THIS METHOD: To allow another pane/widget to display parameters independently
    public void setOnObjectDetected(Consumer<HumanBeingUiModel> onObjectDetected) {
        this.onObjectDetected = onObjectDetected == null ? h -> {
        } : onObjectDetected;
    }

    private void animateAppearance() {
        animationScale.set(0.1);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(animationScale, 0.1)),
                new KeyFrame(Duration.millis(450), new KeyValue(animationScale, 1.0))
        );
        timeline.play();
    }

    private void createGridPattern() {
        double size = 40;
        Canvas tempCanvas = new Canvas(size, size);
        GraphicsContext tempGc = tempCanvas.getGraphicsContext2D();

        // Ensure no background is drawn here, only draw grid lines
        tempGc.setStroke(Color.web("#142e14"));
        tempGc.setLineWidth(1);
        tempGc.strokeLine(0, 0, size, 0);
        tempGc.strokeLine(0, 0, 0, size);

        // TRANSPARENT CONFIG: Force snapshot not to automatically fill with white background
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        WritableImage img = new WritableImage((int) size, (int) size);
        tempCanvas.snapshot(params, img); // Fix 'null' to 'params'

        this.gridPattern = new ImagePattern(img, 0, 0, size, size, false);
    }

    private void createRadarSweepImage() {
        Canvas tempCanvas = new Canvas(RADAR_MAX_SIZE * 2, RADAR_MAX_SIZE * 2);
        GraphicsContext tempGc = tempCanvas.getGraphicsContext2D();
        double cx = RADAR_MAX_SIZE;
        double cy = RADAR_MAX_SIZE;

        // Draw fading radar tail
        for (int i = 0; i < TAIL_SIZE; i++) {
            double currentArcAngle = (-i + 360) % 360;
            double alpha = (1.0 - ((double) i / TAIL_SIZE)) * 0.28;
            if (alpha <= 0) continue;

            RadialGradient radarGrad = new RadialGradient(
                    0, 0, cx, cy, RADAR_MAX_SIZE, false, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(34, 255, 34, alpha)),
                    new Stop(0.4, Color.rgb(15, 120, 15, alpha * 0.5)),
                    new Stop(1, Color.rgb(0, 0, 0, 0))
            );
            tempGc.setFill(radarGrad);
            tempGc.fillArc(cx - RADAR_MAX_SIZE, cy - RADAR_MAX_SIZE,
                    RADAR_MAX_SIZE * 2, RADAR_MAX_SIZE * 2,
                    currentArcAngle, 1.5, ArcType.ROUND);
        }

        // TRANSPARENT CONFIG: Helps the radar sweep trail glide smoothly over the dark background
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        this.radarSweepImage = new WritableImage((int) (RADAR_MAX_SIZE * 2), (int) (RADAR_MAX_SIZE * 2));
        tempCanvas.snapshot(params, this.radarSweepImage); // Fix 'null' to 'params'
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();
        gc.clearRect(0, 0, w, h);

        gc.setFill(Color.web("#040d04"));
        gc.fillRoundRect(0, 0, w, h, 18, 18);

        drawGrid(gc, w, h);
        drawRadarSweep(gc, w, h);

        for (HumanBeingUiModel item : items) {
            drawObject(gc, item, w, h);
        }
    }

    private void drawGrid(GraphicsContext gc, double w, double h) {
        double centerX = w / 2 + offsetX;
        double centerY = h / 2 + offsetY;

        // 1. DRAW INFINITE GRID FROM BUFFER (Takes exactly 1 rectangle draw command)
        if (gridPattern != null) {
            // Apply mouse drag offset (offsetX % 40) to the pattern starting point
            double patternAnchorX = offsetX % 40;
            double patternAnchorY = offsetY % 40;

            // Create a dynamic ImagePattern that shifts along with the mouse drag
            ImagePattern shiftedPattern = new ImagePattern(
                    gridPattern.getImage(),
                    patternAnchorX, patternAnchorY, 40, 40, false
            );

            gc.setFill(shiftedPattern);
            gc.fillRect(0, 0, w, h); // Cover the entire Canvas with a super lightweight square grid
        }

        // 2. Draw military-style concentric circles of the Radar at the actual center
        gc.setStroke(Color.web("#1a441a"));
        gc.setLineWidth(1.2);
        for (int r = 80; r <= 4000; r += 80) {
            gc.strokeOval(centerX - r, centerY - r, r * 2, r * 2);
        }

        // 3. Draw the 2 main coordinate axes passing through the radar center
        gc.setStroke(Color.web("#226622"));
        if (centerX >= 0 && centerX <= w) gc.strokeLine(centerX, 0, centerX, h);
        if (centerY >= 0 && centerY <= h) gc.strokeLine(0, centerY, w, centerY);
    }

    private void drawRadarSweep(GraphicsContext gc, double w, double h) {
        if (radarSweepImage == null) return;

        double centerX = w / 2 + offsetX;
        double centerY = h / 2 + offsetY;

        gc.save(); // 1. Save the current coordinate system state

        // 2. Move the coordinate system center to the exact Radar center (Including drag offsetX/Y)
        gc.translate(centerX, centerY);

        // 3. Rotate the entire coordinate system by radarAngle (Ultra-fast GPU processing)
        // Negate to -radarAngle because the Arc axis and JavaFX graphics rotation axis are in opposite directions
        gc.rotate(-radarAngle);

        // 4. Draw the static radar image saved in the buffer on top
        gc.drawImage(radarSweepImage, -RADAR_MAX_SIZE, -RADAR_MAX_SIZE);

        gc.restore(); // 5. Restore the original straight coordinate system to draw other elements
    }

    private void drawObject(GraphicsContext gc, HumanBeingUiModel item, double canvasW, double canvasH) {
        double[] xy = toCanvas(item);
        double maxRadius = radiusOf(item) * animationScale.get();

        // Minimum hidden size (center 00, tiny radius = 2px)
        double minRadius = 2.0;

        Color baseColor = OwnerColorPalette.colorFor(item.ownerLogin());
        Color textColor = OwnerColorPalette.textFor(baseColor);
        boolean selected = selectedId.isPresent() && selectedId.getAsLong() == item.id();

        // - ZOOM IN / OUT ANIMATION SYSTEM BASED ON RADAR SWEEP -
        double centerX = canvasW / 2 + offsetX;
        double centerY = canvasH / 2 + offsetY;

        double objectAngleRad = Math.atan2(centerY - xy[1], xy[0] - centerX);
        double objectAngleDeg = Math.toDegrees(objectAngleRad);
        if (objectAngleDeg < 0) objectAngleDeg += 360;

        double angleDiff = (radarAngle - objectAngleDeg + 360) % 360;

        // Pre-define target variable and sweep ratio outside
        double targetRadius = minRadius;
        double glowFactor = 0.15; // Completely dim when not yet swept
        double sweepRatio = 0.0;  // Default brightness/zoom ratio is 0

        if (angleDiff <= TAIL_SIZE) {
            // When the radar line hits and passes through
            sweepRatio = 1.0 - (angleDiff / TAIL_SIZE);
            targetRadius = minRadius + (maxRadius - minRadius) * sweepRatio;
            glowFactor = 0.15 + sweepRatio * 0.85;
        }

        // Read current radius and calculate interpolation for smooth size transition at 60 FPS
        double currentRadius = animatedRadiusMap.getOrDefault(item.id(), minRadius);
        currentRadius += (targetRadius - currentRadius) * 0.04;
        animatedRadiusMap.put(item.id(), currentRadius);
        // 

        gc.setGlobalAlpha(glowFactor);

//        Draw a brilliant shockwave ripple effect exactly at the edge of the radar beam (precise hit position)
//        if (angleDiff >= 0 && angleDiff <= 3 && maxRadius > minRadius) {
//            gc.setStroke(Color.rgb(255, 255, 255, 0.7));
//            gc.setLineWidth(1.2);
//            gc.strokeOval(xy[0] - maxRadius - 4, xy[1] - maxRadius - 4, maxRadius * 2 + 8, maxRadius * 2 + 8);
//        }
        if (angleDiff >= 0 && angleDiff <= 2) {
            // Check if the object has already been triggered at this angle position in the current sweep
            if (!detectedInCurrentSweep.getOrDefault(item.id(), false)) {
                detectedInCurrentSweep.put(item.id(), true); // Mark as processed

                // TRIGGER CALLBACK: Automatically send the object's parameters to another independent display pane!
                onObjectDetected.accept(item);
            }
        } else if (angleDiff > TAIL_SIZE) {
            // When the radar has completely passed the tail of the sweep, reset the state to prepare for the next sweep
            detectedInCurrentSweep.put(item.id(), false);
        }

        // Draw the circle point (Using the continuously varying currentRadius)
        gc.setFill(baseColor);
        gc.fillOval(xy[0] - currentRadius, xy[1] - currentRadius, currentRadius * 2, currentRadius * 2);

        // Only draw the border and ID text when the size is large enough to avoid a messy UI at the small circle center
        if (currentRadius > 5.0) {
            gc.setStroke(selected ? Color.web("#fafafa") : Color.web("#e4e4e7"));
            gc.setLineWidth(selected ? 3 : 1.5);
            gc.strokeOval(xy[0] - currentRadius, xy[1] - currentRadius, currentRadius * 2, currentRadius * 2);

            if (selected) {
                gc.setStroke(Color.web("#fafafa"));
                gc.setLineWidth(1.0);
                gc.strokeOval(xy[0] - currentRadius - 5, xy[1] - currentRadius - 5, currentRadius * 2 + 10, currentRadius * 2 + 10);
            }

            // Smoothly display the ID text according to the zoom level (sweepRatio range has been fixed)
            gc.setGlobalAlpha(sweepRatio);
            gc.setFill(textColor);
            gc.setFont(Font.font(11));
            String idText = String.valueOf(item.id());
            gc.fillText(idText, xy[0] - idText.length() * 3.0, xy[1] + 4);
        }

        gc.setGlobalAlpha(1.0);
    }

    private void handleMousePressed(MouseEvent event) {
        mouseAnchorX = event.getX();
        mouseAnchorY = event.getY();
        isDragging = false;
    }

    private void handleMouseDragged(MouseEvent event) {
        double deltaX = event.getX() - mouseAnchorX;
        double deltaY = event.getY() - mouseAnchorY;
        if (Math.abs(deltaX) > 3 || Math.abs(deltaY) > 3) {
            isDragging = true;
            offsetX += deltaX;
            offsetY += deltaY;
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (!isDragging) {
            handleClick(event);
        }
        isDragging = false;
    }

    private void handleClick(MouseEvent event) {
        for (HumanBeingUiModel item : items) {
            double[] xy = toCanvas(item);
            double radius = radiusOf(item);
            double dx = event.getX() - xy[0];
            double dy = event.getY() - xy[1];
            if (dx * dx + dy * dy <= radius * radius) {
                setSelectedObject(item);
                onObjectSelected.accept(item);
                return;
            }
        }
        setSelectedObject(null);
    }

    private double[] toCanvas(HumanBeingUiModel item) {
        double scale = 28;
        double x = getWidth() / 2 + item.coordinates().x() * scale + offsetX;
        double y = getHeight() / 2 - item.coordinates().y() * scale + offsetY;
        return new double[]{x, y};
    }

    private double radiusOf(HumanBeingUiModel item) {
        return Math.max(12, Math.min(42, 10 + item.impactSpeed()));
    }
}
