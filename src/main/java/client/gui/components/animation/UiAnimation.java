package main.java.client.gui.components.animation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UiAnimation extends Canvas {
    private final GraphicsContext gc;
    private final List<Image[]> animationSequences;

    private int currentSequenceIdx = 0;
    private int currentFrameIdx = 0;
    private long lastUpdate = 0;
    private final long frameDurationNano = 100_000_000L; // 10 FPS

    public UiAnimation(double width, double height, String[]... icoPaths) {
        super(width, height);
        this.gc = this.getGraphicsContext2D();
        this.animationSequences = new ArrayList<>();

        for (String[] paths : icoPaths) {
            Image[] sequenceImages = new Image[paths.length];
            for (int i = 0; i < paths.length; i++) {

                // 1. Get the URL relative to the resources root directory
                URL resourceUrl = getClass().getResource(paths[i]);

                if (resourceUrl != null) {
                    // 2. Convert to string and enable background loading (true)
                    // Note: No "file:" prefix is needed here! JavaFX detects the correct protocol.
                    sequenceImages[i] = new Image(resourceUrl.toString(), true);
                } else {
                    System.err.println("Warning: Resource not found: " + paths[i]);
                    sequenceImages[i] = null;
                }
            }
            animationSequences.add(sequenceImages);
        }

        // AnimationTimer logic remains exactly the same...
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= frameDurationNano) {
                    updateAndRender();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void updateAndRender() {
        if (animationSequences.isEmpty()) return;
        Image[] currentSeq = animationSequences.get(currentSequenceIdx);
        if (currentSeq.length == 0) return;

        gc.clearRect(0, 0, getWidth(), getHeight());
        Image currentImage = currentSeq[currentFrameIdx];
        if (currentImage != null) {
            gc.drawImage(currentImage, 0, 0, getWidth(), getHeight());
        }
        currentFrameIdx = (currentFrameIdx + 1) % currentSeq.length;
    }
}
