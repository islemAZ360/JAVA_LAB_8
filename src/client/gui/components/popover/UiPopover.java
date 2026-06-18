package client.gui.components.popover;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

/**
 * UiPopover — click-triggered popup panel (shadcn Popover).
 *
 * Usage:
 *   UiPopover pop = new UiPopover();
 *   pop.content().getChildren().add(filterForm);
 *   pop.attachTo(filterBtn);   // toggle on click
 */
public class UiPopover {
    private final Popup popup = new Popup();
    private final VBox contentBox = new VBox(8);

    public UiPopover() {
        contentBox.getStyleClass().add("ui-popover");
        contentBox.setPrefWidth(280);
        popup.getContent().add(contentBox);
        popup.setAutoHide(true);
        popup.setAutoFix(true);
    }

    public VBox content() { return contentBox; }

    public void attachTo(Node trigger) {
        trigger.setOnMouseClicked(e -> {
            if (popup.isShowing()) {
                popup.hide();
            } else {
                var bounds = trigger.localToScreen(trigger.getBoundsInLocal());
                popup.show(trigger, bounds.getMinX(), bounds.getMaxY() + 6);
            }
        });
    }

    public void show(Node anchor) {
        var bounds = anchor.localToScreen(anchor.getBoundsInLocal());
        popup.show(anchor, bounds.getMinX(), bounds.getMaxY() + 6);
    }

    public void hide() { popup.hide(); }
}
