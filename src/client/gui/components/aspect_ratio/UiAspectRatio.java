package client.gui.components.aspect_ratio;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * UiAspectRatio — forces content to maintain aspect ratio (shadcn AspectRatio).
 *
 * Usage:
 *   new UiAspectRatio(16, 9, videoPlayer)   // 16:9 widescreen
 *   new UiAspectRatio(1, 1, squareImage)    // square
 *   new UiAspectRatio(4, 3, previewPane)    // classic
 */
public class UiAspectRatio extends StackPane {
    private final double ratio;

    public UiAspectRatio(double widthRatio, double heightRatio, Node content) {
        getStyleClass().add("ui-aspect-ratio");
        this.ratio = heightRatio / widthRatio;
        if (content != null) getChildren().add(content);
        widthProperty().addListener((obs, o, w) -> setMinHeight(w.doubleValue() * ratio));
    }

    public double getRatio() { return ratio; }
}
