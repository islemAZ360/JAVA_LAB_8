package client.ui.template.components.dialog;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import client.ui.template.core.CssLoader;
import client.ui.template.core.Theme;
import client.ui.template.core.ThemeManager;

public class UiDialog {
    private final Stage stage = new Stage();
    private final BorderPane root = new BorderPane();
    private final VBox content = new VBox(12);
    private final HBox footer = new HBox(8);

    public UiDialog(String title, Window owner) {
        root.getStyleClass().add("ui-dialog");
        content.getStyleClass().add("ui-dialog-content");
        footer.getStyleClass().add("ui-dialog-footer");
        root.setCenter(content);
        root.setBottom(footer);

        Scene scene = new Scene(root, 520, 520);
        CssLoader.applyTo(scene);
        ThemeManager.applyTheme(scene.getRoot(), Theme.DARK);

        stage.setTitle(title == null ? "Dialog" : title);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) stage.initOwner(owner);
    }

    public VBox content() {
        return content;
    }

    public HBox footer() {
        return footer;
    }

    public void setContent(Node node) {
        content.getChildren().setAll(node);
    }

    public void showAndWait() {
        stage.showAndWait();
    }

    public void close() {
        stage.close();
    }

    public Stage stage() {
        return stage;
    }
}
