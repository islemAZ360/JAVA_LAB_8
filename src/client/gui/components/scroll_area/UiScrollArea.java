package client.gui.components.scroll_area;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

/**
 * UiScrollArea — styled scroll container (shadcn ScrollArea).
 *
 * Usage:
 *   UiScrollArea area = new UiScrollArea(myLongContent);
 *   area.setMaxHeight(400);
 */
public class UiScrollArea extends ScrollPane {
    public UiScrollArea(Node content) {
        getStyleClass().add("ui-scroll-area");
        setContent(content);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    }

    public UiScrollArea horizontal() {
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(false);
        setFitToHeight(true);
        return this;
    }

    public UiScrollArea both() {
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        return this;
    }
}
