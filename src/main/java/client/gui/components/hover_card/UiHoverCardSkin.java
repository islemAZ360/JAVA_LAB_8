package main.java.client.gui.components.hover_card;

import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.PopupControl;

public class UiHoverCardSkin implements Skin<PopupControl> {
    private final PopupControl popup;
    private final Node content;

    public UiHoverCardSkin(PopupControl popup, Node content) {
        this.popup = popup;
        this.content = content;
    }

    @Override public PopupControl getSkinnable() { return popup; }
    @Override public Node getNode() { return content; }
    @Override public void dispose() {}
}
