package client.ui.template.components.alert;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class UiAlert extends VBox {
    private final Label title = new Label();
    private final Label message = new Label();
    private AlertVariant variant;

    public UiAlert(String titleText, String messageText, AlertVariant variant) {
        getStyleClass().add("ui-alert");
        title.getStyleClass().add("ui-alert-title");
        message.getStyleClass().add("ui-alert-message");
        message.setWrapText(true);
        getChildren().addAll(title, message);
        setText(titleText, messageText);
        applyVariant(variant);
    }

    public UiAlert setText(String titleText, String messageText) {
        title.setText(titleText == null ? "" : titleText);
        message.setText(messageText == null ? "" : messageText);
        return this;
    }

    public UiAlert applyVariant(AlertVariant newVariant) {
        getStyleClass().removeAll(Arrays.stream(AlertVariant.values()).map(AlertVariant::cssClass).toList());
        this.variant = newVariant == null ? AlertVariant.INFO : newVariant;
        getStyleClass().add(this.variant.cssClass());
        return this;
    }
}
