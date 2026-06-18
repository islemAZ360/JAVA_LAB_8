package client.gui.components.item;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import client.gui.components.badge.BadgeVariant;
import client.gui.components.badge.UiBadge;

/**
 * UiItem — flexible list/feed item row (shadcn Item).
 * Renders: [leading] [title + subtitle] [trailing / badge]
 *
 * Usage:
 *   new UiItem("John Doe", "Administrator")
 *   new UiItem("Server #1", "Running", avatarNode, new UiBadge("Online", BadgeVariant.SUCCESS))
 *   UiItem.divider()
 */
public class UiItem extends HBox {
    private final Label titleLabel    = new Label();
    private final Label subtitleLabel = new Label();

    public UiItem(String title, String subtitle) {
        this(title, subtitle, null, null);
    }

    public UiItem(String title, String subtitle, Node leading, Node trailing) {
        getStyleClass().add("ui-item");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);
        setPadding(new javafx.geometry.Insets(10, 12, 10, 12));

        if (leading != null) {
            leading.getStyleClass().add("ui-item-leading");
            getChildren().add(leading);
        }

        VBox text = new VBox(2);
        titleLabel.setText(title == null ? "" : title);
        titleLabel.getStyleClass().add("ui-item-title");
        subtitleLabel.setText(subtitle == null ? "" : subtitle);
        subtitleLabel.getStyleClass().add("ui-item-subtitle");
        boolean hasSub = subtitle != null && !subtitle.isBlank();
        subtitleLabel.setVisible(hasSub);
        subtitleLabel.setManaged(hasSub);
        text.getChildren().addAll(titleLabel, subtitleLabel);
        HBox.setHgrow(text, Priority.ALWAYS);
        getChildren().add(text);

        if (trailing != null) {
            trailing.getStyleClass().add("ui-item-trailing");
            getChildren().add(trailing);
        }
    }

    public UiItem setOnClick(Runnable action) {
        setOnMouseClicked(e -> { if (action != null) action.run(); });
        getStyleClass().add("ui-item-clickable");
        return this;
    }

    public UiItem setBadge(String text, BadgeVariant variant) {
        getChildren().removeIf(n -> n instanceof UiBadge);
        getChildren().add(new UiBadge(text, variant));
        return this;
    }

    public static javafx.scene.control.Separator divider() {
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        sep.getStyleClass().add("ui-item-divider");
        return sep;
    }
}
