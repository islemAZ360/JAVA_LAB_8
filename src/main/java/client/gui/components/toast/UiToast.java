package main.java.client.gui.components.toast;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import java.util.Arrays;

/**
 * UiToast — transient notification (shadcn Toast / Sonner).
 * Shows in corner, auto-dismisses after timeout.
 *
 * Usage:
 *   UiToast.show(stage, "File saved!", ToastVariant.SUCCESS);
 *   UiToast.show(stage, "Error", "Could not connect", ToastVariant.ERROR, 5000);
 */
public class UiToast {
    private static final double WIDTH = 320;
    private static final double GAP   = 12;

    private UiToast() {}

    public static void show(Window owner, String message, ToastVariant variant) {
        show(owner, null, message, variant, 3000);
    }

    public static void show(Window owner, String title, String message, ToastVariant variant, long durationMs) {
        HBox box = new HBox(10);
        box.getStyleClass().addAll("ui-toast",
            Arrays.stream(ToastVariant.values())
                  .map(ToastVariant::cssClass)
                  .filter(c -> c.equals(variant.cssClass()))
                  .findFirst().orElse(ToastVariant.DEFAULT.cssClass()));
        box.setPadding(new Insets(12, 16, 12, 16));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(WIDTH);

        VBox textBox = new VBox(2);
        if (title != null && !title.isBlank()) {
            Label titleLbl = new Label(title);
            titleLbl.getStyleClass().add("ui-toast-title");
            textBox.getChildren().add(titleLbl);
        }
        Label msgLbl = new Label(message);
        msgLbl.getStyleClass().add("ui-toast-message");
        msgLbl.setWrapText(true);
        textBox.getChildren().add(msgLbl);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        box.getChildren().add(textBox);

        Popup popup = new Popup();
        popup.getContent().add(box);
        popup.setAutoFix(true);

        // Position: bottom-right
        popup.setOnShown(e -> {
            double x = owner.getX() + owner.getWidth()  - WIDTH - GAP;
            double y = owner.getY() + owner.getHeight() - 80;
            popup.setX(x);
            popup.setY(y);
        });

        popup.show(owner);

        // Fade out then hide
        FadeTransition fade = new FadeTransition(Duration.millis(400), box);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.millis(durationMs));
        fade.setOnFinished(e -> popup.hide());
        fade.play();
    }
}
