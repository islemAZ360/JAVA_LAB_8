package client.ui.template.components.sidebar;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import client.ui.template.components.button.ButtonVariant;
import client.ui.template.components.button.UiButton;

import java.util.ArrayList;
import java.util.List;

public class UiSidebar extends VBox {
    private final VBox menu = new VBox(6);
    private final VBox footer = new VBox(6);
    private final List<UiButton> buttons = new ArrayList<>();

    public UiSidebar(String title) {
        getStyleClass().add("ui-sidebar");
        Label titleLabel = new Label(title == null ? "Menu" : title);
        titleLabel.getStyleClass().add("ui-sidebar-title");
        menu.getStyleClass().add("ui-sidebar-menu");
        footer.getStyleClass().add("ui-sidebar-footer");

        getChildren().addAll(titleLabel, menu, footer);
        VBox.setVgrow(menu, Priority.ALWAYS);
    }

    public UiSidebar addItem(String text, Runnable action) {
        UiButton button = new UiButton(text, ButtonVariant.GHOST);
        button.getStyleClass().add("ui-sidebar-item");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            setSelected(button);
            if (action != null) action.run();
        });
        buttons.add(button);
        menu.getChildren().add(button);
        return this;
    }

    public UiSidebar setFooter(Node node) {
        footer.getChildren().setAll(node);
        return this;
    }

    private void setSelected(UiButton selected) {
        for (UiButton button : buttons) {
            button.getStyleClass().remove("ui-sidebar-item-selected");
        }
        selected.getStyleClass().add("ui-sidebar-item-selected");
    }
}
