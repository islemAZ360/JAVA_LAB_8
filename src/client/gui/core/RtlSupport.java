package client.gui.core;

import javafx.scene.Node;
import javafx.scene.Parent;

public final class RtlSupport {
    private RtlSupport() {}

    public static void apply(Node node, Direction direction) {
        node.setNodeOrientation(direction.orientation());
        node.getStyleClass().removeAll("dir-ltr", "dir-rtl");
        node.getStyleClass().add(direction == Direction.RTL ? "dir-rtl" : "dir-ltr");
    }

    public static void applyToRoot(Parent root, Direction direction) {
        apply(root, direction);
    }
}
