package client.gui.components.card;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UiCard extends VBox {
    private final HBox header = new HBox(12);
    private final VBox titleBox = new VBox(3);
    private final VBox content = new VBox(10);
    private final HBox footer = new HBox(8);
    private final Label title = new Label();
    private final Label description = new Label();

    public UiCard() {
        getStyleClass().add("ui-card");
        setSpacing(14);
        setPadding(new Insets(18));

        title.getStyleClass().add("ui-card-title");
        description.getStyleClass().add("ui-card-description");

        titleBox.getChildren().addAll(title, description);

//        Region spacer = new Region();
//        HBox.setHgrow(spacer, Priority.ALWAYS);
//        header.getChildren().addAll(titleBox, spacer);

        header.getChildren().addAll(titleBox);
        header.getStyleClass().add("ui-card-header");
        content.getStyleClass().add("ui-card-content");
        footer.getStyleClass().add("ui-card-footer");

        getChildren().addAll(header, content, footer);

//        Content grow max height (use CSS instead to reuse/ layout isolate with style)
//        VBox.setVgrow(content, javafx.scene.layout.Priority.ALWAYS);
//        content.setMaxHeight(Double.MAX_VALUE);
    }

    public UiCard(String titleText, String descriptionText) {
        this();
        setTitle(titleText);
        setDescription(descriptionText);
    }

    public UiCard setTitle(String value) {
        title.setText(value == null ? "" : value);
        return this;
    }

    public Label getTitleLabel() {
        return this.title;
    }

    public UiCard setDescription(String value) {
        description.setText(value == null ? "" : value);
        description.setVisible(value != null && !value.isBlank());
        description.setManaged(description.isVisible());
        return this;
    }

    public UiCard setAction(Node action) {
        if (header.getChildren().size() > 2) {
            header.getChildren().remove(2, header.getChildren().size());
        }
        if (action != null) header.getChildren().add(action);
        return this;
    }

    public VBox content() {
        return content;
    }

    public HBox footer() {
        return footer;
    }
}
