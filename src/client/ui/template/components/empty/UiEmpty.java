package client.ui.template.components.empty;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import client.ui.template.components.button.ButtonVariant;
import client.ui.template.components.button.UiButton;

public class UiEmpty extends VBox {
    private final Label iconLabel = new Label("∅");
    private final Label title = new Label();
    private final Label description = new Label();

    public UiEmpty(String titleText, String descriptionText) {
        getStyleClass().add("ui-empty");
        setAlignment(Pos.CENTER);
        setSpacing(10);

        iconLabel.getStyleClass().add("ui-empty-icon");
        title.getStyleClass().add("ui-empty-title");
        description.getStyleClass().add("ui-empty-description");
        description.setWrapText(true);

        setText(titleText, descriptionText);
        getChildren().addAll(iconLabel, title, description);
    }

    public UiEmpty setText(String titleText, String descriptionText) {
        title.setText(titleText == null ? "" : titleText);
        description.setText(descriptionText == null ? "" : descriptionText);
        return this;
    }

    public UiEmpty setIcon(Node icon) {
        getChildren().remove(iconLabel);
        if (icon != null) getChildren().add(0, icon);
        return this;
    }

    public UiEmpty addAction(String text, Runnable action) {
        UiButton button = new UiButton(text, ButtonVariant.OUTLINE);
        button.setOnAction(e -> action.run());
        getChildren().add(button);
        return this;
    }
}
