package main.java.client.gui.components.sheet;

import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import main.java.client.gui.core.CssLoader;
import main.java.client.gui.core.Theme;
import main.java.client.gui.core.ThemeManager;

/**
 * UiSheet — slide-in panel from edge (shadcn Sheet).
 *
 * Usage:
 *   UiSheet sheet = new UiSheet("Filter", Side.RIGHT, owner);
 *   sheet.content().getChildren().add(filterForm);
 *   sheet.show();
 */
public class UiSheet {
    private final Stage stage = new Stage();
    private final VBox content = new VBox(12);

    public UiSheet(String title, Side side, Window owner) {
        BorderPane root = new BorderPane(content);
        root.getStyleClass().addAll("ui-sheet", "ui-sheet-" + side.name().toLowerCase());
        content.getStyleClass().add("ui-sheet-content");

        double w = (side == Side.LEFT || side == Side.RIGHT) ? 360 : Double.MAX_VALUE;
        double h = (side == Side.TOP || side == Side.BOTTOM) ? 300 : Double.MAX_VALUE;

        Scene scene = new Scene(root, w, h);
        CssLoader.applyTo(scene);
        ThemeManager.applyTheme(root, Theme.DARK);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) stage.initOwner(owner);
        stage.setScene(scene);
        stage.setTitle(title);
    }

    public VBox content() { return content; }
    public void show() { stage.show(); }
    public void showAndWait() { stage.showAndWait(); }
    public void close() { stage.close(); }
}
