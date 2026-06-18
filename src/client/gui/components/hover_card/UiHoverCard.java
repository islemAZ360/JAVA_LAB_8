package client.gui.components.hover_card;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * UiHoverCard — rich popup shown on hover (shadcn HoverCard).
 *
 * Usage:
 *   UiHoverCard hc = new UiHoverCard();
 *   hc.content().getChildren().add(profileCard);
 *   hc.attachTo(triggerNode);
 */
public class UiHoverCard extends PopupControl {
    private final VBox contentBox = new VBox(8);
    private Timeline delayTimeline;

    public UiHoverCard() {
        contentBox.getStyleClass().add("ui-hover-card");
        contentBox.setPrefWidth(300);

        setSkin(new UiHoverCardSkin(this, contentBox));

        setAutoHide(true);
        setAutoFix(true);
    }

    public VBox content() {
        return contentBox;
    }

    public void attachTo(Node trigger) {
        trigger.setOnMouseEntered(e -> {
            if (delayTimeline != null) {
                delayTimeline.stop();
            }
            if (!isShowing()) {
                var bounds = trigger.localToScreen(trigger.getBoundsInLocal());
                show(trigger, bounds.getMinX(), bounds.getMaxY() + 8);
            }
        });

        trigger.setOnMouseExited(e -> startHideDelay());

        contentBox.setOnMouseEntered(e -> {
            if (delayTimeline != null) {
                delayTimeline.stop();
            }
        });

        contentBox.setOnMouseExited(e -> hide());
    }

    private void startHideDelay() {
        if (delayTimeline != null) {
            delayTimeline.stop();
        }
        delayTimeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            if (!contentBox.isHover()) {
                hide();
            }
        }));
        delayTimeline.play();
    }
}
