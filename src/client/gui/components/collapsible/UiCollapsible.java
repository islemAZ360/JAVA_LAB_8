package client.gui.components.collapsible;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * UiCollapsible — single expand/collapse section (shadcn Collapsible).
 *
 * Usage:
 *   UiCollapsible c = new UiCollapsible("Show details");
 *   c.setContent(detailsNode);
 *   c.toggle();
 */
public class UiCollapsible extends VBox {
    private final Label trigger;
    private final VBox contentBox = new VBox();
    private boolean expanded = false;

    public UiCollapsible(String triggerText) {
        getStyleClass().add("ui-collapsible");
        trigger = new Label(triggerText + " ▾");
        trigger.getStyleClass().add("ui-collapsible-trigger");
        trigger.setOnMouseClicked(e -> toggle());
        contentBox.getStyleClass().add("ui-collapsible-content");
        contentBox.setVisible(false);
        contentBox.setManaged(false);
        getChildren().addAll(trigger, contentBox);
    }

    public UiCollapsible setContent(Node node) {
        contentBox.getChildren().setAll(node);
        return this;
    }

    public void toggle() {
        expanded = !expanded;
        contentBox.setVisible(expanded);
        contentBox.setManaged(expanded);
        trigger.setText(trigger.getText().replaceAll("[▾▴]", expanded ? "▴" : "▾"));
    }

    public boolean isExpanded() { return expanded; }
}
