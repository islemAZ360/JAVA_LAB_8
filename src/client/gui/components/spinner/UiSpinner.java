package client.gui.components.spinner;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class UiSpinner extends StackPane {
    private final ProgressIndicator indicator = new ProgressIndicator();

    public UiSpinner() {
        getStyleClass().add("ui-spinner-wrap");
        indicator.getStyleClass().add("ui-spinner");
        indicator.setMaxSize(36, 36);
        setAlignment(Pos.CENTER);
        getChildren().add(indicator);
    }

    public UiSpinner setSize(double size) {
        indicator.setPrefSize(size, size);
        indicator.setMaxSize(size, size);
        return this;
    }
}
