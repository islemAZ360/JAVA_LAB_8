package main.java.client.gui.components.resizable;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class ResizableSplitPane extends SplitPane {
    public ResizableSplitPane(Orientation orientation, Node... nodes) {
        getStyleClass().add("ui-resizable");
        setOrientation(orientation == null ? Orientation.HORIZONTAL : orientation);
        getItems().addAll(nodes);
    }

    public ResizableSplitPane setDivider(double position) {
        setDividerPositions(position);
        return this;
    }
}
