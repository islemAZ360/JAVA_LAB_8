package client.gui.components.slider;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

/**
 * UiSlider — range slider with label (shadcn Slider).
 *
 * Usage:
 *   UiSlider s = new UiSlider(0, 100, 50, "Volume");
 *   s.valueProperty().addListener((obs, o, n) -> setVolume(n.intValue()));
 */
public class UiSlider extends VBox {
    private final Slider slider;
    private final Label valueLabel = new Label();

    public UiSlider(double min, double max, double value, String label) {
        getStyleClass().add("ui-slider-wrap");
        setSpacing(4);
        Label titleLabel = new Label(label);
        titleLabel.getStyleClass().add("ui-slider-label");
        slider = new Slider(min, max, value);
        slider.getStyleClass().add("ui-slider");
        slider.setMaxWidth(Double.MAX_VALUE);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        valueLabel.getStyleClass().add("ui-slider-value");
        valueLabel.setText(String.valueOf((int) value));
        slider.valueProperty().addListener((obs, o, n) -> valueLabel.setText(String.valueOf(n.intValue())));
        getChildren().addAll(titleLabel, slider, valueLabel);
    }

    public javafx.beans.property.DoubleProperty valueProperty() { return slider.valueProperty(); }
    public double getValue() { return slider.getValue(); }
    public void setValue(double v) { slider.setValue(v); }
}
