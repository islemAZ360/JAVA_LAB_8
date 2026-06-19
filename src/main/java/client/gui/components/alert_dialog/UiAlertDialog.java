package main.java.client.gui.components.alert_dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * UiAlertDialog — confirmation dialog (shadcn AlertDialog).
 *
 * Usage:
 *   UiAlertDialog.confirm(
 *       "Delete item?",
 *       "This cannot be undone.",
 *       () -> doDelete()
 *   );
 */
public class UiAlertDialog {
    private UiAlertDialog() {}

    public static void confirm(String title, String message, Runnable onConfirm) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStyleClass().add("ui-alert-dialog");
        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK && onConfirm != null) onConfirm.run();
        });
    }

    public static void info(String title, String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getStyleClass().add("ui-alert-dialog");
        dialog.showAndWait();
    }
}
