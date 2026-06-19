package main.java.client.gui.components.progress;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * UiProgress — labeled progress bar (shadcn Progress).
 *
 * Usage:
 *   UiProgress p = new UiProgress();
 *   p.setProgress(0.65);   // 65%
 *   p.setLabel("Uploading...");
 */
public class UiProgress extends VBox {
    private final ProgressBar bar = new ProgressBar(0);
    private final Label label = new Label();

    public UiProgress() {
        getStyleClass().add("ui-progress-wrap");
        bar.getStyleClass().add("ui-progress");
        bar.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add("ui-progress-label");
        setSpacing(4);
        getChildren().addAll(label, bar);
    }

    public UiProgress setProgress(double value) {
        bar.setProgress(Math.max(0, Math.min(1, value)));
        return this;
    }

    public UiProgress setLabel(String text) {
        label.setText(text == null ? "" : text);
        label.setVisible(!label.getText().isBlank());
        return this;
    }

    public UiProgress setIndeterminate() { bar.setProgress(-1); return this; }
    public double getProgress() { return bar.getProgress(); }
}
