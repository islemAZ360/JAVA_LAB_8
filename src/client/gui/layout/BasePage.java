package client.gui.layout;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Abstract base class for all dashboard pages.
 * Provides common layout: title + description + content.
 * Subclasses only need to add their specific content.
 */
public abstract class BasePage extends VBox {

    protected final Label titleLabel;
    protected final Label descriptionLabel;

    public BasePage(String title, String description) {
        this.setSpacing(16);
        this.setPadding(new Insets(18));
        this.getStyleClass().add("page");

        // Title label
        this.titleLabel = new Label(title);
        this.titleLabel.getStyleClass().add("page-title");

        // Description label
        this.descriptionLabel = new Label(description);
        this.descriptionLabel.getStyleClass().add("page-description");
        this.descriptionLabel.setWrapText(true);

        this.getChildren().addAll(titleLabel, descriptionLabel);
    }

    /**
     * Wrap content in a ScrollPane (useful for long pages).
     */
    protected ScrollPane wrapInScroll(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("page-scroll");
        return scrollPane;
    }

    /**
     * Helper to set vertical grow priority for a child node.
     */
    protected void setVGrow(Node node) {
        VBox.setVgrow(node, Priority.ALWAYS);
    }
}
